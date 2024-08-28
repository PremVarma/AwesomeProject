package com.awesomeproject

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.facebook.react.ReactActivity
import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule

private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045
private const val REQUEST_CODE_MEDIA_PERMISSION = 1002

class MainActivity : ReactActivity() {

    private var screenshotObs: ScreenshotObs? = null
    

    companion object {
        const val REQUEST_CODE_PERMISSION = 1
    }

    private val screenCaptureCallback =
        Activity.ScreenCaptureCallback {
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
        screenshotObs?.let { contentResolver.unregisterContentObserver(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper())
            .postDelayed(
                {
                    if (!Environment.isExternalStorageManager()) {
                        checkManageAllFilesAccessPermission()
                    }

                    // Check if notification access is granted
                    if (!isNotificationServiceEnabled()) {
                        // Prompt user to enable notification access
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    val reactContext =
                        (application as MainApplication)
                            .reactNativeHost
                            .reactInstanceManager
                            .currentReactContext

                    reactContext?.let {
                        screenshotObs = ScreenshotObs(contentResolver, reactContext, Handler())
                        contentResolver.registerContentObserver(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            true,
                            screenshotObs!!
                        )
                        emitEvent(it)
                    }
                },
                3000
            )
    }

    fun checkManageAllFilesAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!isManageAllFilesAccessPermissionGranted()) {
                requestManageAllFilesAccessPermission()
            }
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners.contains(packageName)
    }

    private fun isManageAllFilesAccessPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
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
