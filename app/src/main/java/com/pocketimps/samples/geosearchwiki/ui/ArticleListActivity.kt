package com.pocketimps.samples.geosearchwiki.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import com.pocketimps.samples.R
import com.pocketimps.samples.geosearchwiki.dagger.AppComponent
import com.pocketimps.samples.geosearchwiki.dagger.ArticleListModule
import com.pocketimps.samples.geosearchwiki.data.model.GeoSearchResultList
import com.pocketimps.samples.geosearchwiki.ui.controller.DataLoadController
import com.pocketimps.samples.geosearchwiki.ui.controller.DataLoadListener
import com.pocketimps.samples.geosearchwiki.ui.controller.ObtainLocationController
import com.pocketimps.samples.geosearchwiki.ui.controller.ObtainLocationListener
import com.pocketimps.samples.geosearchwiki.util.hide
import com.pocketimps.samples.geosearchwiki.util.show
import kotlinx.android.synthetic.main.activity_articles.*
import javax.inject.Inject

class ArticleListActivity : BaseActivity() {
    override val mContentResId = R.layout.activity_articles

    @Inject
    lateinit var obtainLocationController: ObtainLocationController

    private val mObtainLocationListener = object : ObtainLocationListener {
        override fun onLocationObtained(location: Location?) {
            dataLoadController.loadArticles(location)
        }

        override fun requestLocationPermissions(): Boolean {
            if (ActivityCompat.checkSelfPermission(this@ArticleListActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                return false

            ActivityCompat.requestPermissions(this@ArticleListActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return true
        }

        override fun showProgress() = showProgress(R.string.progress_location)
    }


    @Inject
    lateinit var dataLoadController: DataLoadController

    private val mDataLoadListener = object : DataLoadListener {
        override fun setResults(results: GeoSearchResultList) {
            swipe_to_refresh.isRefreshing = false

            mAdapter.setItems(results)

            if (results.isEmpty()) {
                progress_indicator.hide()
                progress_message.setText(R.string.message_empty)
            } else {
                article_list.show()
                progress_frame.hide()
            }
        }

        override fun onError() {
            swipe_to_refresh.isRefreshing = false
            progress_indicator.hide()
            progress_message.setText(R.string.message_fetch_failed)
        }

        override fun showProgress() = showProgress(R.string.progress_fetch)
    }

    private val mAdapter = ArticleListAdapter()
    
    override fun injectDependenciesTo(component: AppComponent) {
        component.plus(ArticleListModule(mObtainLocationListener, mDataLoadListener)).injectTo(this)
    }

    private fun showProgress(textId: Int) {
        article_list.hide()
        progress_frame.show()
        progress_indicator.show()
        progress_message.setText(textId)
    }

    private fun refresh() {
        obtainLocationController.getCurrentLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        article_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        article_list.adapter = mAdapter

        swipe_to_refresh.setOnRefreshListener {
            val running = (obtainLocationController.isInProgress() || dataLoadController.isInProgress())
            if (running)
                swipe_to_refresh.isRefreshing = false
            else
                refresh()
        }

        refresh()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val granted = (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        obtainLocationController.onLocationPermissionsGrantResult(granted)
    }

    override fun onDestroy() {
        super.onDestroy()

        obtainLocationController.detach()
        dataLoadController.detach()
    }
}
