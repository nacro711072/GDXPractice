package com.mygdx.practice;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.practice.MyGdxPractice;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.launcher_activity);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		initialize(new MyGdxPractice(), config);
	}
}
