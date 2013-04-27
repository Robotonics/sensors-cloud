package com.sorin.cloudcog.car.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sorin.cloudcog.R;
import com.sorin.cloudcog.cosmpull.Login;
import com.sorin.cloudcog.cosmpush.CosmAndroidResourcesActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class CarMainFragmentActivityRuby<ImageView> extends FragmentActivity {
	CarTabFragmentHandlerRuby mRubyAdapter;
	ViewPager mRubyPager;
	PageIndicator mRubyIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.car_pager_main_ruby);

		mRubyAdapter = new CarTabFragmentHandlerRuby(
				getSupportFragmentManager());

		mRubyPager = (ViewPager) findViewById(R.id.pager_ruby);
		mRubyPager.setAdapter(mRubyAdapter);

		mRubyIndicator = (TitlePageIndicator) findViewById(R.id.indicator_ruby);
		mRubyIndicator.setViewPager(mRubyPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.car_ruby_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/**
		 * the following switch statement will execute based on chosen option
		 * and will trigger the appropriate intents
		 * 
		 */
		// starts the silver style gauges main fragment activity
		case R.id.action_silver_gauges:
			Intent silverIntent = new Intent(this,
					CarMainFragmentActivitySilver.class);
			silverIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(silverIntent);

			Toast.makeText(this, "Silver Light Style", Toast.LENGTH_SHORT)
					.show();

			break;

		case R.id.action_usb:
			startActivityForResult(
					new Intent(
							android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS),
					0);
			Toast.makeText(this, "Turn On/Off USB debugging",
					Toast.LENGTH_SHORT).show();

			break;
		case R.id.action_wifi:

			startActivityForResult(new Intent(
					android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
			Toast.makeText(this, "Manage wifi connection", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.action_location:

			startActivityForResult(new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
					0);
			Toast.makeText(this, "Manage Location sources", Toast.LENGTH_SHORT)
					.show();
			break;

		case R.id.action_bluetooth:
			startActivityForResult(new Intent(
					android.provider.Settings.ACTION_BLUETOOTH_SETTINGS), 0);
			Toast.makeText(this, "Manage bluetooth connections",
					Toast.LENGTH_SHORT).show();

			break;
		case R.id.action_cosm_web:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.cosm.com"));
			startActivity(browserIntent);
			Toast.makeText(this, "Access your personal Cosm account",
					Toast.LENGTH_SHORT).show();

			break;

		case R.id.action_cosm_push:
			startActivity(new Intent(this, CosmAndroidResourcesActivity.class));
			Toast.makeText(this, "Push live data to Cosm", Toast.LENGTH_SHORT)
					.show();

			break;

		case R.id.action_cosm_pull:

			Intent mainIntent = new Intent(CarMainFragmentActivityRuby.this,
					Login.class);
			mainIntent.putExtra("flag", "true");
			CarMainFragmentActivityRuby.this.startActivity(mainIntent);
			Toast.makeText(this, "Pull live data from Cosm", Toast.LENGTH_SHORT)
					.show();
			return true;
		default:

			break;
		}
		return true;
	}
}
