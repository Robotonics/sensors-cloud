/*
    Copyright (c) 2012-2013 Darshan-Josiah Barber

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.sorin.cloudcog.phone.batterymeter;

public class PredictorCore {
	public static final int DISCHARGE = 0;
	public static final int RECHARGE_AC = 1;
	public static final int RECHARGE_WL = 2;
	public static final int RECHARGE_USB = 3;

	public static final int ONE_MINUTE = 60 * 1000;
	public static final int FIVE_MINUTES = ONE_MINUTE * 5;
	public static final int TEN_MINUTES = ONE_MINUTE * 10;
	public static final int FIFTEEN_MINUTES = ONE_MINUTE * 15;
	public static final int THIRTY_MINUTES = ONE_MINUTE * 30;
	public static final int ONE_HOUR = ONE_MINUTE * 60;
	public static final int TWO_HOURS = ONE_HOUR * 2;
	public static final int THREE_HOURS = ONE_HOUR * 3;
	public static final int FOUR_HOURS = ONE_HOUR * 4;
	public static final int SIX_HOURS = ONE_HOUR * 6;
	public static final int EIGHT_HOURS = ONE_HOUR * 8;
	public static final int TWELVE_HOURS = ONE_HOUR * 12;
	public static final int ONE_DAY = ONE_HOUR * 24;
	public static final int SINCE_STATUS_CHANGE = -1;

	public static final int TYPE_FIVE_MINUTES = 1;
	public static final int TYPE_TEN_MINUTES = 2;
	public static final int TYPE_FIFTEEN_MINUTES = 3;
	public static final int TYPE_THIRTY_MINUTES = 4;
	public static final int TYPE_ONE_HOUR = 5;
	public static final int TYPE_TWO_HOURS = 6;
	public static final int TYPE_THREE_HOURS = 7;
	public static final int TYPE_FOUR_HOURS = 8;
	public static final int TYPE_SIX_HOURS = 9;
	public static final int TYPE_EIGHT_HOURS = 10;
	public static final int TYPE_TWELVE_HOURS = 11;
	public static final int TYPE_ONE_DAY = 12;
	public static final int TYPE_STATUS_CHANGE = 13;
	public static final int TYPE_LONG_TERM = 14;

	private static final double WEIGHT_OLD_AVERAGE = 0.998;
	private static final double WEIGHT_NEW_DATA = 1 - WEIGHT_OLD_AVERAGE;

	private static final int[] DEFAULT = { 24 * 60 * 60 * 1000 / 100,
			3 * 60 * 60 * 1000 / 100, 4 * 60 * 60 * 1000 / 100,
			6 * 60 * 60 * 1000 / 100 };

	private int recent_duration = FIVE_MINUTES;
	private int prediction_type = TYPE_FIVE_MINUTES;

	private long[] timestamps = new long[101];
	private int ts_head;

	private double[] average = new double[4];

	private double recent_average;

	private BatteryInfo cur_info;
	private int last_level;
	private int last_status = -1; // Impossible value, so first update knows
									// it's the first update
	private int last_plugged;
	private long last_prediction;
	private int dir_inc; // -1 if charging; 1 if discharging; unspecified
							// otherwise. For iterating over timestamps.
	private long now;
	private boolean partial;
	private boolean initial;

	public int cur_charging_status; // TODO make getters and make private

	public PredictorCore(float ave_discharge, float ave_recharge_ac,
			float ave_recharge_wl, float ave_recharge_usb) {
		average[DISCHARGE] = ave_discharge == -1 ? DEFAULT[DISCHARGE]
				: ave_discharge;
		average[RECHARGE_AC] = ave_recharge_ac == -1 ? DEFAULT[RECHARGE_AC]
				: ave_recharge_ac;
		average[RECHARGE_WL] = ave_recharge_wl == -1 ? DEFAULT[RECHARGE_WL]
				: ave_recharge_wl;
		average[RECHARGE_USB] = ave_recharge_usb == -1 ? DEFAULT[RECHARGE_USB]
				: ave_recharge_usb;
	}

	public void update(BatteryInfo info, long when) {
		cur_info = info;
		cur_charging_status = chargingStatusForCurInfo();
		now = when;

		if (info.status != last_status
				|| info.plugged != last_plugged
				|| info.status == BatteryInfo.STATUS_FULLY_CHARGED
				|| (info.status == BatteryInfo.STATUS_CHARGING && info.percent < ts_head)
				|| (info.status == BatteryInfo.STATUS_UNPLUGGED && info.percent > ts_head)) {
			initial = true;
			partial = false;

			ts_head = info.percent;
			dir_inc = info.status == BatteryInfo.STATUS_CHARGING ? -1 : 1;

			timestamps[info.percent] = now;
			recent_average = average[cur_charging_status];

			updateInfoPrediction();
			return;
		}

		if ((info.status == BatteryInfo.STATUS_CHARGING && info.percent < last_level)
				|| (info.status == BatteryInfo.STATUS_UNPLUGGED && info.percent > last_level)) {
			partial = false;
			timestamps[info.percent] = now;
			updateInfoPrediction();
			return;
		}

		int level_diff = Math.abs(last_level - info.percent);

		if (level_diff == 0) {
			if (shouldUsePartial())
				partial = true;
			else
				return;
		} else {
			partial = false;
			double ms_diff = (double) (now - timestamps[last_level]);
			double ms_per_point = ms_diff / level_diff;

			for (int i = 0; i < level_diff; i += 1)
				timestamps[info.percent + (i * dir_inc)] = now
						- (long) (i * ms_per_point);

			// Initial level change may happen promptly and should not shorten
			// prediction
			if (initial && ms_per_point < recent_average) {
				initial = false;
				ts_head = info.percent;
				setLasts();
				return;
			}

			initial = false;

			for (int i = 0; i < level_diff; i++)
				average[cur_charging_status] = average[cur_charging_status]
						* WEIGHT_OLD_AVERAGE + ms_per_point * WEIGHT_NEW_DATA;
		}

		updateInfoPrediction();
	}

	public double getLongTermAverage() {
		return average[cur_charging_status];
	}

	private boolean shouldUsePartial() {
		if (partial)
			return true;

		double ms_diff = (double) (now - timestamps[last_level]);
		if (ms_diff <= recent_average)
			return false;
		if (predictionIfPartial() <= last_prediction)
			return false;
		return true;
	}

	private long predictionIfPartial() {
		boolean old_partial = partial;
		partial = true;
		long ret = prediction();
		partial = old_partial;
		return ret;
	}

	private void updateInfoPrediction() {
		last_prediction = prediction();
		cur_info.prediction.update(last_prediction);
		setLasts();
	}

	private long prediction() {
		recent_average = recentAverage();

		if (cur_info.status == BatteryInfo.STATUS_CHARGING)
			return whenCharged();
		else if (cur_info.status == BatteryInfo.STATUS_UNPLUGGED)
			return whenDrained();
		else
			return 0;
	}

	private long whenDrained() {
		int level = cur_info.percent;
		long from = timestamps[cur_info.percent];

		if (partial) {
			level -= dir_inc;
			from = now;
		}

		return from + (long) (recent_average * level);
	}

	private long whenCharged() {
		int level = cur_info.percent;
		long from = timestamps[cur_info.percent];

		if (partial) {
			level -= dir_inc;
			from = now;
		}

		return from + (long) ((100 - level) * recent_average);
	}

	private void setLasts() {
		last_level = cur_info.percent; // TODO: Resolve level/percent
										// discrepancy?
		last_status = cur_info.status;
		last_plugged = cur_info.plugged;
	}

	private double recentAverage() {
		double total_points = 0d;
		double total_ms = 0d;
		double needed_ms = recent_duration;

		int start = cur_info.percent;
		if (partial)
			start -= dir_inc;

		for (int i = start; i != ts_head; i += dir_inc) {
			double t;

			if (i == start && partial)
				t = now - timestamps[cur_info.percent];
			else
				t = timestamps[i] - timestamps[i + dir_inc];

			if (t > needed_ms) {
				total_points += needed_ms / t;
				total_ms += needed_ms;
				needed_ms = 0;
				break;
			}

			total_points += 1;
			total_ms += t;
			needed_ms -= t;
		}

		if (needed_ms > 0)
			total_points += needed_ms / average[cur_charging_status];

		return recent_duration / total_points;
	}

	private int chargingStatusForCurInfo() {
		if (cur_info.status == BatteryInfo.STATUS_CHARGING) {
			if (cur_info.plugged == BatteryInfo.PLUGGED_USB)
				return RECHARGE_USB;
			else if (cur_info.plugged == BatteryInfo.PLUGGED_WIRELESS)
				return RECHARGE_WL;
			else
				return RECHARGE_AC;
		} else {
			return DISCHARGE;
		}
	}
}
