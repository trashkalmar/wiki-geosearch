package com.pocketimps.samples.geosearchwiki.app

import android.app.Application
import com.pocketimps.samples.geosearchwiki.dagger.DaggerAppComponent

class App : Application() {
    val component by lazy(LazyThreadSafetyMode.NONE) {
        DaggerAppComponent.create()!!
    }


    override fun onCreate() {
        super.onCreate()
        sInstance = this
    }


    companion object {
        private lateinit var sInstance: App

        fun app() = sInstance
    }
}
