package com.sorin.cloudcog.geolocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sorin.cloudcog.R;
import com.sorin.cloudcog.ShakeDetectorActivity;
import com.sorin.cloudcog.ShakeDetectorActivity.OnShakeListener;
import com.sorin.cloudcog.cosmpull.CustomOverlay;
import com.sorin.cloudcog.cosmpull.Login;
import com.sorin.cloudcog.cosmpush.CosmAndroidResourcesActivity;

public class MapRouteActivity extends FragmentActivity implements
		LocationListener {

	GoogleMap map;
	ArrayList<LatLng> markerPoints;

	LocationManager locationManager;
	CustomOverlay itemizedOverlay;
	// The following are used for the shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetectorActivity mShakeDetector;
	// coordinates
	private static final long MIN_TIME = 400;
	private static final float MIN_DISTANCE = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_route_view);

		Toast.makeText(this, "GPS Position tracking and Data loging",
				Toast.LENGTH_SHORT).show();
		// Getting Google Play availability status

		// ShakeDetector initialization
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetectorActivity();
		mShakeDetector.setOnShakeListener(new OnShakeListener() {

			@Override
			public void onShake(int count) {
				// Closes main activity when shaking phone
				MapRouteActivity.this.finish();

			}

		});

		if (map == null) {
			// Try to obtain the map from the SupportMapFragment.
			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (map != null) {
				map.setMyLocationEnabled(true);

			}
		}

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

		// Getting reference to the SupportMapFragment of activity_main.xml
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		// Getting GoogleMap object from the fragment
		map = fm.getMap();

		// Enabling MyLocation Layer of Google Map
		map.setMyLocationEnabled(true);

		// Getting LocationManager object from System Service
		// LOCATION_SERVICE
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location
		android.location.Location location = locationManager
				.getLastKnownLocation(provider);

		if (location != null) {
			onLocationChanged(location);
		}
		locationManager.requestLocationUpdates(provider, 2000, 1000, this);

		// Initializing
		markerPoints = new ArrayList<LatLng>();

		// Getting reference to SupportMapFragment of the activity_main
		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		// Getting Map for the SupportMapFragment
		map = fm.getMap();

		// Enable MyLocation Button in the Map
		map.setMyLocationEnabled(true);

		// Setting onclick event listener for the map
		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {

				// Already two locations
				if (markerPoints.size() > 1) {
					markerPoints.clear();
					map.clear();
				}

				// Adding new item to the ArrayList
				markerPoints.add(point);

				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();

				// Setting the position of the marker
				options.position(point);

				/**
				 * For the start location, the color of marker is GREEN and for
				 * the end location, the color of marker is RED.
				 */
				if (markerPoints.size() == 1) {
					options.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				} else if (markerPoints.size() == 2) {
					options.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				}

				// Add new marker to the Google Map Android API V2
				map.addMarker(options);

				// Checks, whether start and end locations are captured
				if (markerPoints.size() >= 2) {
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);

					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);

					DownloadTask downloadTask = new DownloadTask();

					// Start downloading json data from Google Directions
					// API
					downloadTask.execute(url);
				}

			}
		});
	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);

		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(7);
				lineOptions.color(R.color.blue);

			}

			// Drawing polyline in the Google Map for the i-th route
			map.addPolyline(lineOptions);
		}
	}

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
	public void onLocationChanged(android.location.Location location) {

		// Getting latitude of the current location
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		// Showing the current location in Google Map
		map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		/**
		 * draw marker // Marker cosmMarker = map.addMarker(new //
		 * MarkerOptions().position(latLng) // .title("Cosm Feed") //
		 * .snippet("This is the current location of the Feed") // // ); //
		 * cosmMarker.showInfoWindow();
		 */
		// Zoom in the Google Map
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
		locationManager.removeUpdates(this);

	}

	@Override
	public void onProviderDisabled(String provider) {

		Toast.makeText(getApplicationContext(), "Gps Disabled",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(), "Gps Enabled",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.geolocation_main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/**
		 * the following switch statement will execute based on chosen optio and
		 * will trigger the appropriate intents
		 */

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

			Intent mainIntent = new Intent(this, Login.class);
			mainIntent.putExtra("flag", "true");
			this.startActivity(mainIntent);

			return true;
		default:

			break;
		}
		return true;
	}
}