package com.sorin.cloudcog.xivelypull;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sorin.cloudcog.R;

public class FeedData extends Activity {

	TextView feedID;
	TextView description;
	TextView domain;
	// TextView status;
	TextView updated;
	// TextView datastreams;

	ImageView liveicon;
	String[] tags;
	String[] DID;

	// private MapController mapController;
	// private MapView mapView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xivelypull_feed_data);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		feedID = (TextView) this.findViewById(R.id.widget39);
		description = (TextView) this.findViewById(R.id.widget41);
		domain = (TextView) this.findViewById(R.id.widget43);
		// status = (TextView)this.findViewById(R.id.widget45);
		updated = (TextView) this.findViewById(R.id.widget47);
		// datastreams = (TextView)this.findViewById(R.id.widget49);

		liveicon = (ImageView) this.findViewById(R.id.widget50);

		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		String desc = settings.getString("description", "0");
		String stat = settings.getString("status", "0");
		String fID = settings.getString("feed", "0");
		String date = settings.getString("updated", "0");
		String dom = settings.getString("domain", "0");
		String lat = settings.getString("lat", "0");
		String lon = settings.getString("lon", "0");

		int size = Integer.parseInt(settings.getString("size", "0"));
		String data = "";
		tags = new String[size];

		DID = new String[size];

		if (!fID.equals("0")) {
			feedID.setText(fID);
			description.setText(desc);
			domain.setText(dom);
			// status.setText(stat);

			if (stat.equals("live")) {
				liveicon.setImageResource(R.drawable.live);
			} else {
				liveicon.setImageResource(R.drawable.frozen);
			}
			updated.setText(date);

			LinearLayout layout = (LinearLayout) findViewById(R.id.widget135);

			for (int i = 0; i < size; i++) {
				DID[i] = settings.getString("D" + i, "" + i);
				tags[i] = settings.getString("Tag" + i, "unknown");
				data += DID[i] + " --> " + tags[i] + ": "
						+ settings.getString("" + i, "0") + "\r\n";

				// Gauge gauge = (Gauge) findViewById(R.id.Gauge);
				Gauge gauge = new Gauge(this);

				LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);

				gauge.scaleUpperTitle = "Datastream #" + DID[i];
				gauge.scaleLowerTitle = tags[i] + ": "
						+ settings.getString("" + i, "0");

				gauge.scaleMaxValue = (int) Double.parseDouble(settings
						.getString("Max" + DID[i], "1000"));
				gauge.scaleMinValue = (int) Double.parseDouble(settings
						.getString("Min" + DID[i], "0"));

				/*
				 * if(gauge.scaleMaxValue > 800) gauge.incrementPerLargeNotch =
				 * gauge.scaleMaxValue/100; else if(gauge.scaleMaxValue > 200)
				 * gauge.incrementPerLargeNotch = gauge.scaleMaxValue/20; else
				 */
				// gauge.incrementPerLargeNotch = gauge.scaleMaxValue/10;
				// gauge.scaleMinValue = 0;

				// gauge.incrementPerSmallNotch = gauge.incrementPerLargeNotch;
				gauge.scaleCenterValue = (int) ((gauge.scaleMaxValue + Double
						.parseDouble(settings.getString("Min" + DID[i], "0"))) / 2);

				if (gauge.scaleMaxValue > 500) {
					gauge.scaleMaxValue = 1024;
					gauge.scaleMinValue = 500;
					gauge.scaleCenterValue = 800;

				}

				else {

					gauge.scaleMaxValue *= 2;
				}
				// gauge.scaleMinValue = 0;
				// gauge.incrementPerSmallNotch = 2;
				// gauge.incrementPerLargeNotch = 10;

				System.out.println("Max=" + gauge.scaleMaxValue);
				System.out.println("Min=" + gauge.scaleMinValue);
				System.out.println("Inc=" + gauge.incrementPerLargeNotch);
				System.out.println("Center=" + gauge.scaleCenterValue);

				String[] parse = settings.getString("" + i, "0").split(" - ");

				gauge.setHandTarget(Float.parseFloat(parse[0]));

				layout.addView(gauge, p);
			}

			// datastreams.setText(data);

		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

}
