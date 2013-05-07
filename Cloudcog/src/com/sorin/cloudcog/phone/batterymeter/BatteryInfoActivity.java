/*
    Copyright (c) 2013 Darshan-Josiah Barber

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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.sorin.cloudcog.R;
import com.sorin.cloudcog.ShakeDetectorActivity;
import com.sorin.cloudcog.ShakeDetectorActivity.OnShakeListener;

public class BatteryInfoActivity extends FragmentActivity {
	private BatteryInfoPagerAdapter pagerAdapter;
	private ViewPager viewPager;

	public static CurrentInfoFragment currentInfoFragment;
	public static LogViewFragment logViewFragment;
	private long startMillis;
	// The following are used for the shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetectorActivity mShakeDetector;

	public Context context;
	public Resources res;
	public Str str;
	public SharedPreferences settings;
	public SharedPreferences sp_store;

	private static final String LOG_TAG = "BatteryBot";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		startMillis = System.currentTimeMillis();

		context = getApplicationContext();
		res = getResources();
		str = new Str(res);
		loadSettingsFiles();

		super.onCreate(savedInstanceState); // Recreates Fragments, so only call
											// after doing necessary setup

		setContentView(R.layout.battery_info);

		pagerAdapter = new BatteryInfoPagerAdapter(getSupportFragmentManager());
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(pagerAdapter);

		PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
		tabStrip.setTabIndicatorColor(0x33b5e5);

		viewPager.setCurrentItem(1);
		// ShakeDetector initialization
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetectorActivity();
		mShakeDetector.setOnShakeListener(new OnShakeListener() {

			@Override
			public void onShake(int count) {

				BatteryInfoActivity.this.finish();

			}
		});
		// ShakeDetector method end
	}

	// onResume and onPause are meant for shake events
	@Override
	public void onResume() {
		super.onResume();
		// Add the following line to register the Session Manager Listener
		// onResume
		mSensorManager.registerListener(mShakeDetector, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void onPause() {
		// Add the following line to unregister the Sensor Manager onPause
		mSensorManager.unregisterListener(mShakeDetector);
		super.onPause();
	}

	public void loadSettingsFiles() {
		settings = context.getSharedPreferences(SettingsActivity.SETTINGS_FILE,
				Context.MODE_MULTI_PROCESS);
		sp_store = context.getSharedPreferences(SettingsActivity.SP_STORE_FILE,
				Context.MODE_MULTI_PROCESS);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && viewPager.getCurrentItem() != 1) {
			viewPager.setCurrentItem(1);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public class BatteryInfoPagerAdapter extends FragmentPagerAdapter {
		public BatteryInfoPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 2; // TODO
		}

		// TODO: Put Fragment types and page titles in Arrays or Map or
		// something.

		@Override
		public Fragment getItem(int position) {
			if (position == 1) {
				if (currentInfoFragment == null)
					currentInfoFragment = new CurrentInfoFragment();
				return currentInfoFragment;
			} else {
				if (logViewFragment == null)
					logViewFragment = new LogViewFragment();
				return logViewFragment;
			}
		}

		public CharSequence getPageTitle(int position) {
			if (position == 1) {
				return res.getString(R.string.tab_current_info).toUpperCase();
			} else {
				return res.getString(R.string.tab_history).toUpperCase();

			}

		}

	}
}
