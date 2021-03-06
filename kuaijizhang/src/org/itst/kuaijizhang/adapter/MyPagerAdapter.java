package org.itst.kuaijizhang.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MyPagerAdapter extends PagerAdapter {
	private List<View> list;
	@Override
	public int getCount() {
		return list.size();
	}

	public MyPagerAdapter(List<View> list) {
		super();
		this.list=list;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(list.get(position));
	}
	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager)container).addView(list.get(position),0);
		return list.get(position);
	}
}
