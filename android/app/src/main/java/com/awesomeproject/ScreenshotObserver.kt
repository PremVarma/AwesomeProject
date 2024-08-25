package com.awesomeproject

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule

class ScreenshotObserver(private val reactContext: ReactContext, handler: Handler) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        // Emit an event when a new image is detected
        emitEvent("screenshotEvent", "Screenshot detected")
    }

    private fun emitEvent(eventName: String, message: String) {
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, message)
    }
}
