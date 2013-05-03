package com.sorin.cloudcog.cosmpull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sorin.cloudcog.R;
import com.sorin.cloudcog.ShakeDetectorActivity;
import com.sorin.cloudcog.ShakeDetectorActivity.OnShakeListener;
import com.sorin.cloudcog.cosmpush.CosmAndroidResourcesActivity;
import com.sorin.cloudcog.geolocation.GeoLocationActivity;

@SuppressWarnings({ "unused", "deprecation" })
public class Login extends Activity {
	// login variables
	private Button loginBtn;
	ProgressDialog myProgressDialog = null;
	EditText username;
	EditText password;
	EditText feed;
	Handler hdl;
	boolean live;
	// The following are used for the shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetectorActivity mShakeDetector;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cosmpull_login);
		Toast.makeText(this, "Pull live data from Cosm", Toast.LENGTH_SHORT)
				.show();
		// ShakeDetector initialization
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetectorActivity();
		mShakeDetector.setOnShakeListener(new OnShakeListener() {
			// Closes activity when shaken
			@Override
			public void onShake(int count) {

				Login.this.finish();

			}
		});

		loginBtn = (Button) this.findViewById(R.id.btnlogin);
		// loginBtn.setAlpha(255);
		username = (EditText) this.findViewById(R.id.edtxtusrname);
		password = (EditText) this.findViewById(R.id.edtxtpasswd);
		feed = (EditText) this.findViewById(R.id.edtxtfeed);

		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		String user = settings.getString("username", "0");
		String pass = settings.getString("password", "0");
		String fID = settings.getString("feed", "0");

		// keeps the inputed login values on the login screen after new app
		// restart

		if (!user.equals("0")) {
			username.setText(user);
		}

		if (!pass.equals("0")) {
			password.setText(pass);
		}
		if (!fID.equals("0")) {
			feed.setText(fID);
		}

		Bundle extras = getIntent().getExtras();
		String flag = extras.getString("flag");
		if (flag.equals("false")) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();

			alertDialog.setTitle("Login Error");

			alertDialog
					.setMessage("Please check your credential or Internet connection");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {

					return;

				}
			});
		}

		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// hdl = new Handler();
				// Thread thread = new Thread(Application.this);
				// thread.start();

				// Make a runnable

				Runnable myRunnable = new Runnable() {
					@Override
					public void run() {
						Looper.prepare();
						// toogleFetchBtn(false);

						int fetch = fetchData(username.getText().toString(),
								password.getText().toString(), feed.getText()
										.toString());

						if (fetch == 0) {

							myProgressDialog.dismiss();

							Intent errorLogin = new Intent(Login.this,
									Login.class);
							errorLogin.putExtra("flag", "false");

							startActivityForResult(errorLogin, 0);

						} else {
							myProgressDialog.dismiss();

							// keep it:
							final String PREFS_NAME = "MyPrefsFile";
							SharedPreferences settings;
							SharedPreferences.Editor editor;
							settings = getSharedPreferences(PREFS_NAME, 0);
							editor = settings.edit();
							editor.putString("username", username.getText()
									.toString());
							editor.putString("password", password.getText()
									.toString());
							editor.putString("feed", feed.getText().toString());
							editor.commit();

							Intent feedata = new Intent(Login.this, main.class);
							feedata.putExtra("feed", feed.getText().toString());
							feedata.putExtra("live", live);
							startActivityForResult(feedata, 1);
							// toogleFetchBtn(true);
						}

					}
				};

				// open up you dialog
				myProgressDialog = ProgressDialog.show(Login.this,
						"Cosm Feed Viewer", "fetching data...", true);
				// initialize your thread
				Thread thread = new Thread(null, myRunnable, "FetchData");
				thread.start();
			}
		});

	}

	// methods for shake movement
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

	void toogleFetchBtn(boolean state) {
		if (state) {
			loginBtn.setEnabled(true);
		} else {
			loginBtn.setEnabled(false);
		}
	}

	int fetchData(String username, String password, String feedID) {
		int a = 0;

		if (username.length() > 0 && password.length() > 0
				&& feedID.length() > 0)
			a = runJSONParser(feedID, username, password);
		return a;
	}

	public InputStream getJSONData(String url, final String user,
			final String pass) {

		InputStream data = null;
		try {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, pass.toCharArray());
				}
			});

			HttpURLConnection c = (HttpURLConnection) new URL(url)
					.openConnection();
			/*
			 * c.setRequestProperty( "Authorization", "basic " +
			 * android.util.Base64.encode((user+":"+pass).getBytes(),
			 * android.util.Base64.DEFAULT) );
			 */
			c.setUseCaches(false);
			c.connect();

			data = c.getInputStream();
			// System.out.println(readInputStreamAsString(data));
			// Log.i("AUTH", readInputStreamAsString(data));
		} catch (Exception e) {
			Log.i("AUTH", "NOT Authenticated");
			e.printStackTrace();
		}

		return data;
	}

	public static String readInputStreamAsString(InputStream in)
			throws IOException {

		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			byte b = (byte) result;
			buf.write(b);
			result = bis.read();
		}
		return buf.toString();
	}

	public int runJSONParser(String feed, String username, String password) {
		try {
			Log.i("MY INFO", "Json Parser started..");
			Gson gson = new Gson();
			Reader r = new InputStreamReader(getJSONData(
					"http://api.pachube.com/v2/feeds/" + feed + ".json",
					username, password));
			if (r == null)
				return 0;
			else {
				EEML objs = gson.fromJson(r, EEML.class);
				Log.i("MY INFO", "" + objs.getDescription());
				Log.i("MY INFO", "" + objs.getLocation());

				Log.i("MY INFO", "" + objs.getStatus());
				Log.i("MY INFO", "" + objs.getUpdated());
				Log.i("MY INFO", "" + objs.getDatastreams().size());

				final String PREFS_NAME = "MyPrefsFile";
				SharedPreferences settings;
				SharedPreferences.Editor editor;
				settings = getSharedPreferences(PREFS_NAME, 0);
				editor = settings.edit();
				editor.putString("description", objs.getDescription());
				editor.putString("status", objs.getStatus());
				if (objs.getStatus().equals("live")) {
					live = true;
				} else {
					live = false;
				}
				editor.putString("updated", objs.getUpdated());
				editor.putString("size", "" + objs.getDatastreams().size());
				editor.putString("domain", "" + objs.getLocation().getDomain());
				editor.putString("lat", "" + objs.getLocation().getLat());
				editor.putString("lon", "" + objs.getLocation().getLon());
				// editor.commit();

				String[] tags;
				String allTags = "";
				int counter = 0;
				String unit;
				for (datastream dt : objs.getDatastreams()) {
					allTags = "";
					if (dt.getUnits() == null) {
						unit = "";
					} else {
						unit = dt.getUnits().label;
					}
					Log.i("Datastream", dt.getCurrent_Value() + " - " + unit);
					editor.putString("" + dt.getID(), dt.getCurrent_Value()
							+ " - " + unit);
					editor.putString("Min" + dt.getID(), dt.getMin_Value());
					editor.putString("Max" + dt.getID(), dt.getMax_Value());
					editor.putString("D" + counter, dt.getID());

					tags = dt.getTags();
					if (tags != null) {
						for (int i = 0; i < tags.length - 1; i++) {
							allTags += tags[i] + ", ";
						}
						allTags += tags[tags.length - 1];
					} else {
						allTags = "";
					}

					editor.putString("Tag" + counter, allTags);
					counter++;

				}

				editor.commit();
				// loginBtn.setEnabled(true);
				return 1;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cosmpull_main_menu, menu);
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

			startActivity(new Intent(this, GeoLocationActivity.class));
			break;
		case R.id.action_nfc:

			startActivityForResult(
					new Intent(
							android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS),
					0);
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

			Intent mainIntent = new Intent(Login.this,
					CosmAndroidResourcesActivity.class);
			mainIntent.putExtra("flag", "true");
			Login.this.startActivity(mainIntent);
			Toast.makeText(this, "Push live data from Cosm", Toast.LENGTH_SHORT)
					.show();
			return true;
		default:

			break;
		}
		return true;
	}

}
