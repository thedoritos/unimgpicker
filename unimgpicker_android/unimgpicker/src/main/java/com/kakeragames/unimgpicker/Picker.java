package com.kakeragames.unimgpicker;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import java.io.ByteArrayOutputStream;
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

		String realPath;
		Context context = getActivity().getApplicationContext();

		if (Build.VERSION.SDK_INT < 11)
			// SDK < API11
			realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(context, data.getData());
		else if (Build.VERSION.SDK_INT < 19)
			// SDK >= 11 && SDK < 19
			realPath = RealPathUtil.getRealPathFromURI_API11to18(context, data.getData());
		else
			// SDK > 19 (Android 4.4)
			realPath = RealPathUtil.getRealPathFromURI_API19(context, data.getData());

		UnityPlayer.UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD, realPath);
	}
}
