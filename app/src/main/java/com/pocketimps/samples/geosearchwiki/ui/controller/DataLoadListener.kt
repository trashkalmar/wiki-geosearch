package com.pocketimps.samples.geosearchwiki.ui.controller

import com.pocketimps.samples.geosearchwiki.data.model.GeoSearchResultList

/**
 * Notifications about loaded articles around specified location.
 */
interface DataLoadListener {
    fun setResults(results: GeoSearchResultList)
    fun onError()

    fun showProgress()
}
