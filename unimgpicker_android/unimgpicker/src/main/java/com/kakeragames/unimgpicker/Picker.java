package com.kakeragames.unimgpicker;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.common.io.ByteStreams;
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

	public static void show(String title) {
		Activity unityActivity = UnityPlayer.currentActivity;
		if (unityActivity == null) {
			// TODO: Notify failure.
			return;
		}

		Fragment fragment = new Picker();
		FragmentTransaction transaction = unityActivity.getFragmentManager().beginTransaction();

		transaction.add(fragment, TAG);
		transaction.commit();

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
		intent.setType("image/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		fragment.startActivityForResult(Intent.createChooser(intent, title), REQUEST_CODE);
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
			// TODO: Notify failure.
			return;
		}

		Uri uri = data.getData();
		Context context = getActivity().getApplicationContext();
		String outputName = "uimg";

		try {
			InputStream inputStream = context.getContentResolver().openInputStream(uri);
			FileOutputStream outputStream = context.openFileOutput(outputName, Context.MODE_PRIVATE);
			ByteStreams.copy(inputStream, outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// TODO: Notify failure.
			return;
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: Notify failure.
			return;
		}

		File output = context.getFileStreamPath(outputName);

		UnityPlayer.UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD, output.getPath());
	}
}
