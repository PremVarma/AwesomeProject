package com.awesomeproject

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class ScreenshotAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            // Listen for window state changes which might indicate a screenshot
            Log.e("ScreenshotService",event.eventType.toString())
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                Log.d("ScreenshotService", "Window state changed: ${event.packageName}")
                
                // You can further analyze the event to determine if it might be related to a screenshot
                // For example, checking the package name or window content
            }
        }
    }

    override fun onInterrupt() {
        // Handle service interruptions
    }
}
