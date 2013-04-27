package com.sorin.cloudcog.car.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CarTabFragmentHandlerRuby extends FragmentPagerAdapter {

	protected static final String[] CONTENT = new String[] { "RPM", "KMH",
			"Engine Load", "Intake Temperature", "Coolant Temperature",
			"Voltage" };

	private int mCount = CONTENT.length;

	public CarTabFragmentHandlerRuby(FragmentManager fm) {

		super(fm);
	}

	public Fragment getItem(int position) {
		Fragment fragmentRuby = new RPMFragmentRuby();
		switch (position) {
		case 0:
			fragmentRuby = new RPMFragmentRuby();
			break;
		case 1:
			fragmentRuby = new KMHFragmentRuby();
			break;
		case 2:
			fragmentRuby = new EngineLoadFragmentRuby();
			break;
		case 3:
			fragmentRuby = new IntakeFragmentRuby();
			break;
		case 4:
			fragmentRuby = new CoolantFragmentRuby();
			break;
		case 6:
			fragmentRuby = new VoltageFragmentRuby();
			break;

		}
		return fragmentRuby;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		String titleRuby = "";
		switch (position) {
		case 0:
			titleRuby = "RPM";
			break;
		case 1:
			titleRuby = "KMH";
			break;
		case 2:
			titleRuby = "Engine Load";
			break;

		case 3:
			titleRuby = "Intake Temperature";
			break;

		case 4:
			titleRuby = "Coolant Temperature";
			break;
		case 5:
			titleRuby = "Voltage";
			break;
		}

		return titleRuby;
	}

	public void setCount(int count) {
		if (count > 0 && count < 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}

}
