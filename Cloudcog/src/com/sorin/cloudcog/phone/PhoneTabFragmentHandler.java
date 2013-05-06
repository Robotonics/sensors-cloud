package com.sorin.cloudcog.phone;

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
			pageFragment = new PhoneDataFragment();
			break;
		case 1:
			pageFragment = new PhoneDataFragment();
			break;
		case 2:
			pageFragment = new PhoneCPUFragment();
			break;
		case 3:
			pageFragment = SupportMapFragment.newInstance();

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
			title = "Battery %";
			break;
		case 1:
			title = "Datat t/r";
			break;
		case 2:
			title = "CPU %";
			break;
		case 3:
			title = "Location";
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
