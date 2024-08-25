package com.awesomeproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule

class ScreenshotReceiver(private val reactContext: ReactContext) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("screenshotDetected", null)
    }
}
