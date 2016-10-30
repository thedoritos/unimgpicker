package com.kakeragames.unimgpicker;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;

import com.unity3d.player.UnityPlayer;

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

		Fragment fragment = new Fragment();
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

		Uri uri = data.getData();
		UnityPlayer.UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD, uri.toString());
	}
}
