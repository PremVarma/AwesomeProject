package com.awesomeproject

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.facebook.react.ReactActivity
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.modules.core.DeviceEventManagerModule
import android.database.ContentObserver
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.awesomeproject.ScreenshotObs
import android.content.Intent
import android.provider.Settings
import android.os.Environment


private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045
private const val REQUEST_CODE_MEDIA_PERMISSION = 1002

class MainActivity : ReactActivity() {

    private var screenshotObs: ScreenshotObs? = null

    companion object {
        const val REQUEST_CODE_PERMISSION = 1
    }

    private val screenCaptureCallback = Activity.ScreenCaptureCallback {
        // Add logic to take action in your app when a screenshot is detected
        Log.d("ScreenCapture", "Screen capture detected")
    }


    override fun getMainComponentName(): String? {
        return "AwesomeProject" // This should match the name of your main React component
    }

    override fun onStart() {
        super.onStart()
        // Register the screen capture callback
        registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)
    }

    override fun onStop() {
        super.onStop()
        // Unregister the screen capture callback
        unregisterScreenCaptureCallback(screenCaptureCallback)
    }

    override fun onDestroy() {
            super.onDestroy()
            // unregister observer
            screenshotObs?.let {
        contentResolver.unregisterContentObserver(it)
    }
        }

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         // Check for permissions and request if necessary
         Log.d("MainActivity", "onCreate called")
         

        Handler(Looper.getMainLooper()).postDelayed({
            // Ensure ReactContext is available
            if (!Environment.isExternalStorageManager()) {
                checkManageAllFilesAccessPermission();
            }
            val reactContext = (application as MainApplication).reactNativeHost.reactInstanceManager.currentReactContext

               // create observe


            reactContext?.let {
                // Emit event with a delay of 3 seconds (3000 milliseconds)
                screenshotObs = ScreenshotObs(contentResolver,reactContext, Handler())
                // register observer
                contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenshotObs!!)
                emitEvent(it)
            }
        }, 3000)
    }

    fun checkManageAllFilesAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!isManageAllFilesAccessPermissionGranted()) {
                requestManageAllFilesAccessPermission()
            }
        }
    }

    private fun isManageAllFilesAccessPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true // This permission is not applicable for versions below Android 11
        }
    }

    private fun requestManageAllFilesAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


   private fun emitEvent(reactContext: ReactContext) {
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("mainActivityEvent", "MainActivity was created with a delay")
    }
}
