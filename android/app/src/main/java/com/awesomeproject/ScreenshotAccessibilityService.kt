package com.awesomeproject


import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.accessibility.AccessibilityEvent


class ScreenshotAccessibilityService : AccessibilityService() {
    private var lastModifiedTime: Long = 0
    private var lastScreenshotId: Long = 0

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.e("EventScreenShot", event.eventType.toString())
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            // Detect a screenshot event
            if (isScreenshotTaken()) {
                // Send a broadcast to notify the app
                Log.e("ScreenShot", "Success")

                val intent = Intent("com.awesomeproject.SCREENSHOT_TAKEN")
                sendBroadcast(intent)
            }else{
            Log.e("ScreenShot", "Error unregistering screenshot receiver")
            }
        }
    }

    private fun isScreenshotTaken(): Boolean {
        val contentResolver: ContentResolver = contentResolver
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED)
        val selection = "${MediaStore.Images.Media.DATE_ADDED} > ?"
        val selectionArgs = arrayOf((System.currentTimeMillis() / 1000 - 60).toString()) // Check within the last minute

        var newScreenshotDetected = false

        try {
            val cursor: Cursor? = contentResolver.query(uri, projection, selection, selectionArgs, null)
            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val dateAdded = it.getLong(dateAddedColumn)

                    if (dateAdded > lastScreenshotId) {
                        lastScreenshotId = dateAdded
                        newScreenshotDetected = true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ScreenshotService", "Error querying MediaStore", e)
        }

        return newScreenshotDetected
    }

    override fun onInterrupt() {
        // Handle if the service is interrupted
    }
}
