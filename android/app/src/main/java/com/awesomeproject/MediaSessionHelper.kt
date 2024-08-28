package com.awesomeproject

import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager

class MediaSessionHelper(private val context: Context) {

    private val mediaSessionManager: MediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager

    fun getActiveMediaSessions(callback: (title: String?, artist: String?, timestamp: Long?) -> Unit) {
        val mediaSessions = mediaSessionManager.getActiveSessions(null)
        mediaSessions.forEach { mediaController ->
            val mediaController = MediaController(context, mediaController.sessionToken)
            mediaController.registerCallback(object : MediaController.Callback() {
                override fun onMetadataChanged(metadata: MediaMetadata?) {
                    super.onMetadataChanged(metadata)
                    metadata?.let {
                        val title = it.getString(MediaMetadata.METADATA_KEY_TITLE)
                        val artist = it.getString(MediaMetadata.METADATA_KEY_ARTIST)
                        val timestamp = System.currentTimeMillis() // Replace with actual timestamp if available
                        callback(title, artist, timestamp)
                    }
                }
            })
        }
    }
}