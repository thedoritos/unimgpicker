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

    public static void show(String title, String outputFileName) {
        Activity unityActivity = UnityPlayer.currentActivity;
        if (unityActivity == null) {
            Picker.NotifyFailure("Failed to open the picker");
            return;
        }

        Picker picker = new Picker();
        picker.mTitle = title;
        picker.mOutputFileName = outputFileName;

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
        if (uri == null) {
            Picker.NotifyFailure("Failed to pick the image");
            return;
        }

        Context context = getActivity().getApplicationContext();

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                Picker.NotifyFailure("Failed to find the image");
                return;
            }

            try (FileOutputStream outputStream = context.openFileOutput(mOutputFileName, Context.MODE_PRIVATE)) {
                byte[] buffer = new byte[1024];
                int readLength;
                while ((readLength = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readLength);
                }
            }
        } catch (FileNotFoundException e) {
            Picker.NotifyFailure("Failed to find the image");
            return;
        } catch (IOException e) {
            Picker.NotifyFailure("Failed to copy the image");
            return;
        }

        File output = context.getFileStreamPath(mOutputFileName);
        Picker.NotifySuccess(output.getPath());
    }
}
