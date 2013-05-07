package com.sorin.cloudcog.car.view;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sorin.cloudcog.R;
import com.sorin.cloudcog.ShakeDetectorActivity;
import com.sorin.cloudcog.ShakeDetectorActivity.OnShakeListener;
import com.sorin.cloudcog.cosmpull.Login;
import com.sorin.cloudcog.cosmpush.CosmAndroidResourcesActivity;
import com.sorin.cloudcog.geolocation.MapRouteActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class CarMainFragmentActivitySilver extends FragmentActivity {
	CarTabFragmentHandlerSilver mSilverAdapter;
	ViewPager mSilverPager;
	PageIndicator mSilverIndicator;
	// The following are used for the shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetectorActivity mShakeDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.car_pager_main_silver);
		Toast.makeText(this, "Silver Light style OBD2 data console",
				Toast.LENGTH_SHORT).show();
		mSilverAdapter = new CarTabFragmentHandlerSilver(
				getSupportFragmentManager());

		mSilverPager = (ViewPager) findViewById(R.id.pager_silver);
		mSilverPager.setAdapter(mSilverAdapter);

		mSilverIndicator = (TitlePageIndicator) findViewById(R.id.indicator_silver);
		mSilverIndicator.setViewPager(mSilverPager);

		// When shkaed it will return to main activity initialization
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetectorActivity();
		mShakeDetector.setOnShakeListener(new OnShakeListener() {
			// shake handler
			@Override
			public void onShake(int count) {

				CarMainFragmentActivitySilver.this.finish();

			}
		});
	}

	// onResume and onPause are ment for shake events
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.car_silver_menu, menu);
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
		case R.id.action_geolocation:

			startActivity(new Intent(this, MapRouteActivity.class));

			break;
		// starts the ruby red gauges main fragment activity
		case R.id.action_ruby_gauges:
			Intent rubyIntent = new Intent(this,
					CarMainFragmentActivityRuby.class);
			rubyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			CarMainFragmentActivitySilver.this.finish();
			startActivity(rubyIntent);

			break;
		case R.id.action_nfc:

			startActivityForResult(new Intent(
					android.provider.Settings.ACTION_NFC_SETTINGS), 0);
			Toast.makeText(this, "Beam NFC Tag", Toast.LENGTH_SHORT).show();

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

			break;

		case R.id.action_cosm_pull:

			Intent mainIntent = new Intent(CarMainFragmentActivitySilver.this,
					Login.class);
			mainIntent.putExtra("flag", "true");
			CarMainFragmentActivitySilver.this.startActivity(mainIntent);

			return true;
		default:

			break;
		}
		return true;
	}
}
