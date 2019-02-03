package ru.rienel.clicker;

import android.view.animation.Animation;

public abstract class RotateAnimation implements Animation.AnimationListener {
	@Override
	public void onAnimationStart(Animation animation) {

	}

	@Override
	public void onAnimationEnd(Animation animation) {

	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	public abstract void onAnimationStop(Animation animation);
}
