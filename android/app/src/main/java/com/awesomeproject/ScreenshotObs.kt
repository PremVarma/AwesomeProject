package com.awesomeproject

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

class ScreenshotObs(
    private val contentResolver: ContentResolver,
    private val reactContext: ReactContext,
    handler: Handler
) : ContentObserver(handler) {

    private var mediaSessionHelper: MediaSessionHelper = MediaSessionHelper(reactContext)

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)

        if (
            uri != null &&
                uri.toString().contains(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())
        ) {
            contentResolver
                .query(uri, arrayOf(MediaStore.Images.Media.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val fileNameColumnIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                        val fileName = cursor.getString(fileNameColumnIndex)

                        if (fileName.toLowerCase().contains("screenshot")) {
                            Log.d("ScreenshotObserver", "Screenshot detected: $fileName")
                            emitScreenshotDetectedEvent()
                        } else {
                            Log.d(
                                "ScreenshotObserver",
                                "New image detected but not a screenshot: $fileName"
                            )
                        }
                    } else {
                        Log.d("ScreenshotObserver", "Cursor is empty for URI: $uri")
                    }
                }
        }
    }

    private fun emitScreenshotDetectedEvent() {
        try {
            val title = MediaInfo.currentTitle
            val artist = MediaInfo.currentArtist
            val timestamp = MediaInfo.currentTimestamp
            Log.e("MediaInfo",title+timestamp.toString())
            if (title != null && timestamp != null) {
            val eventData: WritableMap = Arguments.createMap().apply {
                putString("title", title)
                putDouble("timestamp", timestamp.toDouble())
               }
                Log.e("MediaInfo", "$title, $timestamp")
                
                if (reactContext.hasActiveCatalystInstance()) {
                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                        .emit("screenshotDetected", eventData)
                }
            } else {
                Log.d("ScreenshotObserver", "No active media session found, skipping event emission.")
            }
        } catch (e: Exception) {
            Log.e("ScreenshotObserver", "Error emitting event: ${e.message}", e)
        }
    }
}
