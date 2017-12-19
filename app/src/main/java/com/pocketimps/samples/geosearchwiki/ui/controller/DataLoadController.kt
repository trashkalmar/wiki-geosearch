package com.pocketimps.samples.geosearchwiki.ui.controller

import android.location.Location

interface DataLoadController {
    fun isInProgress(): Boolean
    
    fun loadArticles(location: Location?)
    fun detach()
}
