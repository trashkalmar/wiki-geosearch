package com.pocketimps.samples.geosearchwiki.util

import android.os.Handler
import android.os.Looper

object UiThread {
    private val sHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private fun isUiThread() = (Thread.currentThread() === sHandler.looper.thread)

    fun runUi(task: () -> Unit) {
        if (isUiThread())
            task.invoke()
        else
            sHandler.post(task)
    }
}
