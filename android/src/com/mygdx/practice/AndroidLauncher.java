package com.mygdx.practice;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.practice.MyGdxPractice;

public class AndroidLauncher extends AndroidApplication implements View.OnSystemUiVisibilityChangeListener {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.launcher_activity);

        getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
						View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
						View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
						View.SYSTEM_UI_FLAG_FULLSCREEN |
						View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new MyGdxPractice(), config);
	}


	@Override
	public void onSystemUiVisibilityChange(int visibility) {

		if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
							View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
							View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
							View.SYSTEM_UI_FLAG_FULLSCREEN |
							View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
							View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
				}
			}, 1L);
		}
	}
}
