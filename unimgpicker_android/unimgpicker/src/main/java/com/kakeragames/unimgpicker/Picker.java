package com.kakeragames.unimgpicker;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Picker extends Fragment
{
	private static final String TAG = "unimgpicker";
	private static final int REQUEST_CODE = 1;

	private static final String CALLBACK_OBJECT = "Unimgpicker";
	private static final String CALLBACK_METHOD = "OnComplete";
	private static final String CALLBACK_METHOD_FAILURE = "OnFailure";

	private String mTitle;
	private String mOutputFileName;
	private int mMaxSize;

	public static void show(String title, String outputFileName, int maxSize) {
		Activity unityActivity = UnityPlayer.currentActivity;
		if (unityActivity == null) {
			Picker.NotifyFailure("Failed to open the picker");
			return;
		}

		Picker picker = new Picker();
		picker.mTitle = title;
		picker.mOutputFileName = outputFileName;
		picker.mMaxSize = maxSize;

		FragmentTransaction transaction = unityActivity.getFragmentManager().beginTransaction();

		transaction.add(picker, TAG);
		transaction.commit();
	}

	private static void NotifySuccess(String path) {
		UnityPlayer.UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD, path);
	}

	public static void NotifyFailure(String cause) {
		UnityPlayer.UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD_FAILURE, cause);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
		intent.setType("image/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		startActivityForResult(Intent.createChooser(intent, mTitle), REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode != REQUEST_CODE) {
			return;
		}

		FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
		transaction.remove(this);
		transaction.commit();

		if (resultCode != Activity.RESULT_OK || data == null) {
			Picker.NotifyFailure("Failed to pick the image");
			return;
		}

		Uri uri = data.getData();
		Context context = getActivity().getApplicationContext();

		try {
			InputStream inputStream = context.getContentResolver().openInputStream(uri);

			// Decode metadata
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(inputStream, null, opts);
			inputStream.close();

			// Calc size
			float scaleX = Math.min((float)mMaxSize / opts.outWidth, 1.0f);
			float scaleY = Math.min((float)mMaxSize / opts.outHeight, 1.0f);
			float scale = Math.min(scaleX, scaleY);

			float width = opts.outWidth * scale;
			float height = opts.outHeight * scale;

			// Decode image roughly
			inputStream = context.getContentResolver().openInputStream(uri);
			opts = new BitmapFactory.Options();
			opts.inSampleSize = (int)(1.0f / scale);
			Bitmap roughImage = BitmapFactory.decodeStream(inputStream, null, opts);

			// Resize image exactly
			Bitmap image = Bitmap.createScaledBitmap(roughImage, (int) width, (int) height, true);

			// Output image
			FileOutputStream outputStream = context.openFileOutput(mOutputFileName, Context.MODE_PRIVATE);
			image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

			outputStream.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Picker.NotifyFailure("Failed to find the image");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			Picker.NotifyFailure("Failed to copy the image");
			return;
		}

		File output = context.getFileStreamPath(mOutputFileName);
		Picker.NotifySuccess(output.getPath());
	}
}
