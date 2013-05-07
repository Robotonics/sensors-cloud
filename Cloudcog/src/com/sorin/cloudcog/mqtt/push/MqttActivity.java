package com.sorin.cloudcog.mqtt.push;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sorin.cloudcog.R;
import com.sorin.cloudcog.cosmpull.Login;
import com.sorin.cloudcog.cosmpush.CosmAndroidResourcesActivity;
import com.sorin.cloudcog.geolocation.MapRouteActivity;

public class MqttActivity extends Activity {

	public static final String SERVICE_CLASSNAME = "de.eclipsemagazin.mqtt.push.MQTTService";
	private Button button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mqtt_main_view);
		Toast.makeText(this, "Mqtt Push Service on MQTT Broker",
				Toast.LENGTH_SHORT).show();
		button = (Button) findViewById(R.id.button1);
		updateButton();

	}

	@Override
	protected void onResume() {
		super.onResume();
		updateButton();
	}

	private void updateButton() {
		if (serviceIsRunning()) {
			button.setText("Stop Service");
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					button.setText("Start Service");
					stopBlackIceService();
					updateButton();
				}
			});

		} else {
			button.setText("Start Service");
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					button.setText("Stop Service");
					startBlackIceService();
					updateButton();
				}
			});
		}
	}

	private void startBlackIceService() {

		final Intent intent = new Intent(this, MQTTService.class);
		startService(intent);
	}

	private void stopBlackIceService() {

		final Intent intent = new Intent(this, MQTTService.class);
		stopService(intent);
	}

	private boolean serviceIsRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (SERVICE_CLASSNAME.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.phone_main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/**
		 * the following switch statement will execute based on chosen optio and
		 * will trigger the appropriate intents
		 */

		case R.id.action_geolocation:

			startActivity(new Intent(this, MapRouteActivity.class));
			Toast.makeText(this, "Geolocation services", Toast.LENGTH_SHORT)
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

			Intent mainIntent = new Intent(MqttActivity.this, Login.class);
			mainIntent.putExtra("flag", "true");
			MqttActivity.this.startActivity(mainIntent);
			Toast.makeText(this, "Pull live data from Cosm", Toast.LENGTH_SHORT)
					.show();
			return true;
		default:

			break;
		}
		return true;
	}
}
