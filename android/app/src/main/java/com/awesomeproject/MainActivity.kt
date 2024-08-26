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

class MainActivity : ReactActivity() {

  
    private var screenshotObs: ScreenshotObs? = null

    companion object {
        const val REQUEST_CODE_PERMISSION = 1
    }

    private val screenCaptureCallback = Activity.ScreenCaptureCallback {
        // Add logic to take action in your app when a screenshot is detected
        Log.d("ScreenCapture", "Screen capture detected")
    }

  /**
   * Returns the name of the main component registered from JavaScript.
   * This is used to schedule rendering of the component.
   */
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


   private fun emitEvent(reactContext: ReactContext) {
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("mainActivityEvent", "MainActivity was created with a delay")
    }

    private fun emitEventWithDelay(reactContext: ReactContext, delayMillis: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            emitEvent(reactContext)
        }, delayMillis)
    }
}
