package com.awesomeproject

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class ScreenshotAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                Log.d("ScreenshotService", "Window state changed: ${event.packageName}")
            }
        }
    }

    override fun onInterrupt() {
        // Handle service interruptions
    }
}
