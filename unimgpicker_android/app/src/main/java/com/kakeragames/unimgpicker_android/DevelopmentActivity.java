package com.kakeragames.unimgpicker_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kakeragames.unimgpicker.Picker;
import com.unity3d.player.UnityPlayer;

import java.io.File;

public class DevelopmentActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_development);

		UnityPlayer.currentActivity = this;

		Button button = (Button) findViewById(R.id.button_open);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Picker.show("Development", "development", 1024);
			}
		});

		Button reload = (Button) findViewById(R.id.button_reload);
		reload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Context context = DevelopmentActivity.this;

				File image = context.getFileStreamPath("development");
				if (!image.exists()) {
					Toast.makeText(context, "Pick an image first.", Toast.LENGTH_SHORT).show();
					return;
				}

				Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
				ImageView imageView = (ImageView) findViewById(R.id.image_view);
				imageView.setImageBitmap(bitmap);
			}
		});
	}
}
