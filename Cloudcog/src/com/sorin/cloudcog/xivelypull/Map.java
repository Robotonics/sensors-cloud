package com.sorin.cloudcog.xivelypull;

import java.util.List;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.sorin.cloudcog.R;

public class Map extends MapActivity {

	LinearLayout linearLayout;
	MapView mapView;

	List<Overlay> mapOverlays;
	Drawable drawable;
	CustomOverlay itemizedOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xivelypull_map);

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		MapController mapController = mapView.getController();
		mapController.setZoom(4);

		mapOverlays = mapView.getOverlays();

		drawable = this.getResources().getDrawable(R.drawable.map_marker);
		itemizedOverlay = new CustomOverlay(drawable);

		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);

		String lat = settings.getString("lat", "0");
		String lon = settings.getString("lon", "0");

		double latD = Double.parseDouble(lat);
		// latD *= 10000000;
		// int latitude = (int)latD;

		double longD = Double.parseDouble(lon);
		// longD *= 10000000;
		// int longitude = (int)longD;

		// System.out.println(longitude +":::"+latitude);

		GeoPoint point = new GeoPoint((int) (latD * 1E6), (int) (longD * 1E6));
		OverlayItem overlayitem = new OverlayItem(point, "", "");

		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);
		mapController.setCenter(point);

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
