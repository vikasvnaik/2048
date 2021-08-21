package com.game.ui.base

import androidx.multidex.MultiDexApplication
import timber.log.Timber


class AppApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        /*if (BuildConfig.DEBUG) {
            Timber.plant(LineNumberDebugTree())
        } else {
        }*/
    }
}

class LineNumberDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "${element.fileName}:${element.lineNumber}:${element.methodName}"
    }
}