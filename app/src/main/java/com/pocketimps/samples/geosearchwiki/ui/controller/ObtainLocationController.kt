package com.pocketimps.samples.geosearchwiki.ui.controller

interface ObtainLocationController {
    fun isInProgress(): Boolean

    fun getCurrentLocation()
    fun onLocationPermissionsGrantResult(granted: Boolean)
    fun detach()
}
