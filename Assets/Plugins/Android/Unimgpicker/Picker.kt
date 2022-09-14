package com.kakeragames.unimgpicker

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.unity3d.player.UnityPlayer
import java.io.FileNotFoundException
import java.io.IOException

class Picker : Fragment() {
    companion object {
        private const val TAG: String = "unimgpicker"
        private const val REQUEST_CODE: Int = 1

        private const val CALLBACK_OBJECT: String = "Unimgpicker"
        private const val CALLBACK_METHOD: String = "OnComplete"
        private const val CALLBACK_METHOD_FAILURE: String = "OnFailure"

        @JvmStatic
        fun show(title: String, outputFileName: String) {
            val unityActivity = UnityPlayer.currentActivity
            if (unityActivity == null) {
                notifyFailure("Failed to open the picker")
                return
            }

            val picker = Picker()
            picker.mTitle = title
            picker.mOutputFileName = outputFileName

            val transaction = unityActivity.fragmentManager.beginTransaction()

            transaction.add(picker, TAG)
            transaction.commit()
        }

        @JvmStatic
        private fun notifySuccess(path: String) {
            UnityPlayer.UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD, path)
        }

        @JvmStatic
        private fun notifyFailure(cause: String) {
            UnityPlayer.UnitySendMessage(CALLBACK_OBJECT, CALLBACK_METHOD_FAILURE, cause)
        }
    }

    private var mTitle: String = ""
    private var mOutputFileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent()
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        startActivityForResult(Intent.createChooser(intent, mTitle), REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != REQUEST_CODE) {
            return
        }

        val transaction = activity.fragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.commit()

        if (resultCode != Activity.RESULT_OK || data == null) {
            notifyFailure("Failed to pick the image")
            return
        }

        val uri = data.data
        if (uri == null) {
            notifyFailure("Failed to pick the image")
            return
        }

        val context = activity.applicationContext

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                notifyFailure("Failed to find the image")
                return
            }

            val outputStream = context.openFileOutput(mOutputFileName, Context.MODE_PRIVATE)
            val buffer = ByteArray(1024)
            var readLength = 0
            while ((inputStream.read(buffer).also { readLength = it }) > 0) {
                outputStream.write(buffer, 0, readLength)
            }
        } catch (e: FileNotFoundException) {
            notifyFailure("Failed to find the image")
            return
        } catch (e: IOException) {
            notifyFailure("Failed to copy the image")
            return
        }

        val output = context.getFileStreamPath(mOutputFileName)
        notifySuccess(output.path)
    }
}
