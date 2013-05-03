package com.sorin.cloudcog.geolocation;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.Overlay;
import com.sorin.cloudcog.R;
import com.sorin.cloudcog.ShakeDetectorActivity;
import com.sorin.cloudcog.ShakeDetectorActivity.OnShakeListener;
import com.sorin.cloudcog.cosmpull.CustomOverlay;

public class GeoLocationActivity extends FragmentActivity implements
		LocationListener {
	// map constants and variables
	private GoogleMap googleMap;
	private static final long MIN_TIME = 400;
	private static final float MIN_DISTANCE = 1000;
	private LocationManager locationManager;
	List<Overlay> mapOverlays;
	Drawable drawable;
	CustomOverlay itemizedOverlay;
	// The following are used for the shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetectorActivity mShakeDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		Toast.makeText(this, "GPS Position tracking and Data loging",
				Toast.LENGTH_SHORT).show();
		// Getting Google Play availability status

		// ShakeDetector initialization
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetectorActivity();
		mShakeDetector.setOnShakeListener(new OnShakeListener() {
			// Closes activity when shaken
			@Override
			public void onShake(int count) {

				GeoLocationActivity.this.finish();

			}
		});

		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else { // Google Play Services are available

			// Getting reference to the SupportMapFragment of activity_main.xml
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);

			// Getting GoogleMap object from the fragment
			googleMap = fm.getMap();

			// Enabling MyLocation Layer of Google Map
			googleMap.setMyLocationEnabled(true);

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
			locationManager.requestLocationUpdates(provider, 20000, 0, this);
			/* implementing augmented geolocation functionality */

			// FragmentManager myFragmentManager = getSupportFragmentManager();
			// SupportMapFragment mySupportMapFragment = (SupportMapFragment)
			// myFragmentManager
			// .findFragmentById(R.id.map);
			// googleMap = mySupportMapFragment.getMap();
			//
			// googleMap.setOnMapClickListener(this);
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
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// draw marker
		Marker cosmMarker = googleMap.addMarker(new MarkerOptions()
				.position(latLng)
				.title("Cosm Feed")
				.snippet("This is the current location of the Feed")

				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.map_marker)));
		cosmMarker.showInfoWindow();

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app_main_menu, menu);
		return true;
	}

	/* implementing augmented geolocation functionality */

	// @Override
	// public void onMapClick(LatLng point) {
	//
	// googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	// googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	// googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	// googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	//
	// }

}
