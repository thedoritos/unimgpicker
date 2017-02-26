package com.kakeragames.unimgpicker_android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kakeragames.unimgpicker.Picker;
import com.unity3d.player.UnityPlayer;

public class DevelopmentActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_development);

		UnityPlayer.currentActivity = this;

		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Picker.show("Development", "development", 512);
			}
		});
	}
}
