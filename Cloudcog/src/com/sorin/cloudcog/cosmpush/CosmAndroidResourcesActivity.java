package com.sorin.cloudcog.cosmpush;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sorin.cloudcog.R;
import com.sorin.cloudcog.cosmpush.qrscan.IntentIntegrator;
import com.sorin.cloudcog.cosmpush.qrscan.IntentResult;

public class CosmAndroidResourcesActivity extends Activity {
	private Button SaveButton;
	private Button ActivateButton;
	private Button ScanButton;

	EditText id;
	EditText key;

	CheckBox cpu_check;
	CheckBox memory_check;
	CheckBox data_check;
	CheckBox battery_check;

	boolean isRunning;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cosmpush_main);
		final Animation animInverseScaleCosmPush = AnimationUtils.loadAnimation(
				this, R.anim.anim_scale_inverse);
		final Animation animScaleCosm = AnimationUtils.loadAnimation(this,
				R.anim.anim_scale);
		final Animation animAlphaCosm = AnimationUtils.loadAnimation(this,
				R.anim.anim_alpha);
		final Spinner spinner = (Spinner) findViewById(R.id.widget42);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.timeinterval_array,
				android.R.layout.simple_spinner_dropdown_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		// read prefs if exist
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		String s_id = sharedPreferences.getString("FeedID", "");
		String s_key = sharedPreferences.getString("CosmKey", "");
		String s_cpu = sharedPreferences.getString("CPU", "");
		String s_ram = sharedPreferences.getString("RAM", "");
		String s_bat = sharedPreferences.getString("BAT", "");
		String s_time = sharedPreferences.getString("TIME", "");
		String s_data = sharedPreferences.getString("DATA", "");
		//

		SaveButton = (Button) findViewById(R.id.widget44);
		ActivateButton = (Button) findViewById(R.id.widget43);
		ScanButton = (Button) findViewById(R.id.widget45);
		ActivateButton.setEnabled(false);

		id = (EditText) findViewById(R.id.widget33);
		key = (EditText) findViewById(R.id.widget35);

		cpu_check = (CheckBox) findViewById(R.id.widget37);
		memory_check = (CheckBox) findViewById(R.id.widget38);
		data_check = (CheckBox) findViewById(R.id.widget39);
		battery_check = (CheckBox) findViewById(R.id.widget40);

		id.setText(s_id);
		key.setText(s_key);
		if (s_cpu.equals("true"))
			cpu_check.setChecked(true);
		else
			cpu_check.setChecked(false);
		if (s_ram.equals("true"))
			memory_check.setChecked(true);
		else
			memory_check.setChecked(false);
		if (s_bat.equals("true"))
			battery_check.setChecked(true);
		else
			battery_check.setChecked(false);
		if (s_data.equals("true"))
			data_check.setChecked(true);
		else
			data_check.setChecked(false);

		if (s_time.equals("1 minute")) {
			spinner.setSelection(0);
		}
		if (s_time.equals("5 minutes")) {
			spinner.setSelection(1);
		}
		if (s_time.equals("15 minutes")) {
			spinner.setSelection(2);
		}
		if (s_time.equals("30 minutes")) {
			spinner.setSelection(3);
		}
		if (s_time.equals("1 hour")) {
			spinner.setSelection(4);
		}
		if (s_time.equals("2 hours")) {
			spinner.setSelection(5);
		}

		if (!isServiceRunning()) {
			ActivateButton.setText("Turn On");
			isRunning = false;
		} else {
			isRunning = true;
			ActivateButton.setEnabled(true);
			ActivateButton.setText("Turn Off");
		}

		ScanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				arg0.startAnimation(animInverseScaleCosmPush);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {

						IntentIntegrator integrator = new IntentIntegrator(
								CosmAndroidResourcesActivity.this);
						integrator.initiateScan();
					}
				}, 310);

			}
		});

		ActivateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!isRunning) {
					arg0.startAnimation(animInverseScaleCosmPush);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {

							Intent intent = new Intent(
									CosmAndroidResourcesActivity.this,
									Background.class);
							intent.putExtra("FeedID", id.getText().toString());
							intent.putExtra("CosmKey", key.getText().toString());
							intent.putExtra("CPU", "" + cpu_check.isChecked());
							intent.putExtra("RAM",
									"" + memory_check.isChecked());
							intent.putExtra("DATA", "" + data_check.isChecked());
							intent.putExtra("BAT",
									"" + battery_check.isChecked());
							intent.putExtra("TIME",
									"" + (String) spinner.getSelectedItem());
							startService(intent);
							isRunning = true;
							ActivateButton.setText("Turn Off");
						}
					}, 250);
				} else {
					isRunning = false;
					ActivateButton.setText("Turn On");

					arg0.startAnimation(animInverseScaleCosmPush);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							stopService(new Intent(
									CosmAndroidResourcesActivity.this,
									Background.class));
						}
					}, 250);
				}

			}
		});

		SaveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ActivateButton.setEnabled(true);
				arg0.startAnimation(animInverseScaleCosmPush);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {

						// save to prefs:
						SavePreferences("FeedID", id.getText().toString());
						SavePreferences("CosmKey", key.getText().toString());

						SavePreferences("CPU", "" + cpu_check.isChecked());
						SavePreferences("RAM", "" + memory_check.isChecked());
						SavePreferences("DATA", "" + data_check.isChecked());
						SavePreferences("BAT", "" + battery_check.isChecked());

						SavePreferences("TIME",
								(String) spinner.getSelectedItem());

						Toast.makeText(getApplicationContext(),
								"Changes saved!", Toast.LENGTH_LONG).show();
					}
				}, 250);

			}
		});

		Intent intent = new Intent(this, Background.class);
		startService(intent);

	}

	private void SavePreferences(String key, String value) {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public boolean isServiceRunning() {
		final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		final List<RunningServiceInfo> services = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		boolean isServiceFound = true;

		for (int i = 0; i < services.size(); i++) {
			Log.i("", "Service Nr. " + i + " class name : "
					+ services.get(i).service.getClassName());

			if ("com.sorin.cosmpush".equals(services.get(i).service
					.getPackageName())) {
				if ("com.sorin.cosmpush.Background"
						.equals(services.get(i).service.getClassName())) {
					isServiceFound = true;
				}
			}
		}

		return isServiceFound;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult != null) {
			// handle scan result
			String contents = scanResult.getContents();
			key.setText("" + (contents));
		}

	}
}