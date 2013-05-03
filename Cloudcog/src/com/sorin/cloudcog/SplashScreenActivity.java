package com.sorin.cloudcog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity {

	protected boolean _active = true;
	protected int _splashTime = 2000; // time to display the splash screen in ms

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		/** set time to splash out */
		final int SPLASH_DISPLAY_LENGHT = 2000;
		/*
		 * New Handler to start the Menu-Activity and close this Splash-Screen
		 * after some seconds.
		 */
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				/* Create an Intent that will start the Menu-Activity. */
				Intent mainIntent = new Intent(SplashScreenActivity.this,
						CloudcogMainActivity.class);
				mainIntent.putExtra("flag", "true");
				SplashScreenActivity.this.startActivity(mainIntent);
				SplashScreenActivity.this.finish();
			}
		}, SPLASH_DISPLAY_LENGHT);

	}

}
