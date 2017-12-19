package com.pocketimps.samples.geosearchwiki.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Toolbar
import com.pocketimps.samples.R
import com.pocketimps.samples.geosearchwiki.app.App.Companion.app
import com.pocketimps.samples.geosearchwiki.dagger.AppComponent

abstract class BaseActivity : Activity() {
    protected abstract val mContentResId: Int

    protected abstract fun injectDependenciesTo(component: AppComponent)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mContentResId)

        findViewById<Toolbar>(R.id.toolbar)?.let(this::setActionBar)

        injectDependenciesTo(app().component)
    }
}
