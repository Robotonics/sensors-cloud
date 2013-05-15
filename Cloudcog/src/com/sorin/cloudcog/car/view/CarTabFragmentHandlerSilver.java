package com.sorin.cloudcog.car.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CarTabFragmentHandlerSilver extends FragmentPagerAdapter {

	protected static final String[] CONTENT = new String[] { "RPM", "KMH",
			"Engine Load", "Intake Temperature", "Coolant Temperature",
			"Voltage" };

	private int mCount = CONTENT.length;

	public CarTabFragmentHandlerSilver(FragmentManager fm) {

		super(fm);
	}

	public Fragment getItem(int position) {
		Fragment fragmentSilver = new MapFragmentSilver();
		switch (position) {
		case 0:
			fragmentSilver = new RPMFragmentSilver();
			break;
		case 1:
			fragmentSilver = new KMHFragmentSilver();
			break;
		case 2:
			fragmentSilver = new EngineLoadFragmentSilver();
			break;
		case 3:
			fragmentSilver = new IntakeFragmentSilver();
			break;
		case 4:
			fragmentSilver = new CoolantFragmentSilver();
			break;
		case 6:
			fragmentSilver = new VoltageFragmentSilver();
			break;

		}
		return fragmentSilver;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		String titleSilver = "";
		switch (position) {
		case 0:
			titleSilver = "RPM";
			break;
		case 1:
			titleSilver = "KMH";
			break;
		case 2:
			titleSilver = "Engine Load";
			break;
		case 3:
			titleSilver = "Intake Temperature";
			break;
		case 4:
			titleSilver = "Coolant Temperature";
			break;
		case 5:
			titleSilver = "Voltage";
			break;

		}

		return titleSilver;
	}

	public void setCount(int count) {
		if (count > 0 && count < 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}

}
