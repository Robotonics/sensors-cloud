package com.sorin.cloudcog.geolocation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.android.gms.maps.SupportMapFragment;
import com.viewpagerindicator.IconPagerAdapter;

public class PhoneTabFragmentHandler extends FragmentPagerAdapter implements
		IconPagerAdapter {
	protected static final String[] CONTENT = new String[] { "This", "Is", "A",
			"Test" };

	private int mCount = CONTENT.length;

	public PhoneTabFragmentHandler(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment pageFragment;
		switch (position) {
		case 0:
			pageFragment = new PhoneMapFragment1();
			break;
		case 1:
			pageFragment = new PhoneMapFragment2();
			break;
		case 2:
			pageFragment = new PhoneMapFragment3();

			break;
		default:
			pageFragment = null;
			break;
		}
		return pageFragment;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		String title = "";
		switch (position) {
		case 0:
			title = "Map Location 1";
			break;
		case 1:
			title = "Map Location 2";
			break;
		case 2:
			title = "Map Location 3";
			break;

		}

		return title;
	}

	public void setCount(int count) {
		if (count > 0 && count < 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}

}
