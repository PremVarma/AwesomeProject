package com.awesomeproject

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

object MediaInfo {
    var currentTitle: String? = null
    var currentTimestamp: Long? = null
    var currentArtist: String? = null
}

class MediaNotificationListener : NotificationListenerService() {

    private val mediaPackages = listOf(
        "com.spotify.music",
        "com.google.android.apps.youtube.music",
        "com.apple.android.music",
        "com.amazon.mp3",
        "com.pandora.android",
        "app.rvx.android.youtube"
    )

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        val notification = sbn.notification
        val extras = notification.extras

        val title = extras.getString("android.title")
        val timestamp = sbn.postTime
        var packageName = sbn.packageName
        Log.e("MediaNotification",packageName)
        if (mediaPackages.contains(packageName)) {
            title?.let { 
                MediaInfo.currentTitle = it
                MediaInfo.currentTimestamp = timestamp
                Log.d("MediaNotification", "Current media: $title at $timestamp")
            } ?: Log.d("MediaNotification", "Title is null, skipping update")
        }
    }
}