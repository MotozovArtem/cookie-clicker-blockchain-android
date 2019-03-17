package ru.rienel.clicker.common;

import android.support.v4.view.ViewPager;
import android.view.View;

public class CustomPageTransformer implements ViewPager.PageTransformer {

	private ViewPager viewPager;

	public CustomPageTransformer() {
	}

	public void transformPage(View view, float position) {
		if (viewPager == null) {
			viewPager = (ViewPager)view.getParent();
		}
		view.setScaleY(1 - Math.abs(position));
		view.setScaleX(1 - Math.abs(position));
	}
}
