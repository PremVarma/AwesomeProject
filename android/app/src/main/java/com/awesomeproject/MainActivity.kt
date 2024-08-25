package com.awesomeproject

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.facebook.react.ReactActivity
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.modules.core.DeviceEventManagerModule


class MainActivity : ReactActivity() {

  /**
   * Returns the name of the main component registered from JavaScript.
   * This is used to schedule rendering of the component.
   */
  override fun getMainComponentName(): String? {
    return "AwesomeProject" // This should match the name of your main React component
  }

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            // Ensure ReactContext is available
            val reactContext = (application as MainApplication).reactNativeHost.reactInstanceManager.currentReactContext
            reactContext?.let {
                // Emit event with a delay of 3 seconds (3000 milliseconds)
                emitEventWithDelay(it, 3000)
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
