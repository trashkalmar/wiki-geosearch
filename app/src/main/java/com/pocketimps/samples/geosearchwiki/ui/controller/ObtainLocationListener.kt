package com.pocketimps.samples.geosearchwiki.ui.controller

import android.location.Location

/**
 * Notifications about obtained location.
 */
interface ObtainLocationListener {
    /**
     * Resulting location.
     */
    fun onLocationObtained(location: Location?)

    /**
     * Directs view to request location permissions.
     * @return `true` when permissions are not granted now and were requested. `false` otherwise.
     */
    fun requestLocationPermissions(): Boolean
    fun showProgress()
}
