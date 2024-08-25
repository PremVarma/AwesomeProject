package com.awesomeproject

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.uimanager.ViewManager

class ScreenshotPackage : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): List<ReactContextBaseJavaModule> {
        return listOf(ScreenshotModule(reactContext))
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return emptyList()
    }
}
