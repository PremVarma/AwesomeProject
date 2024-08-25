package com.awesomeproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule

class ScreenshotModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    init {
        val receiver = ScreenshotReceiver(reactContext)
        val intentFilter = IntentFilter(Intent.ACTION_USER_PRESENT)
        reactContext.applicationContext.registerReceiver(receiver, intentFilter)
    }

    override fun getName(): String {
        return "ScreenshotModule"
    }
}
