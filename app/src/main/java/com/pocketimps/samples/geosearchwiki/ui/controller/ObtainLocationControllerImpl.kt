package com.pocketimps.samples.geosearchwiki.ui.controller

import android.location.Location
import com.pocketimps.samples.geosearchwiki.app.App.Companion.app
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.rx.ObservableFactory
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ObtainLocationControllerImpl @Inject constructor(listener: ObtainLocationListener)
    : ObtainLocationController {
    private var mListener: ObtainLocationListener? = listener

    private val mPermissionAwaitObservable = PublishSubject.create<Boolean>()
    private var mPermissionAwaitSubscription: Disposable? = null
    private var mLocationAwaitSubscription: Disposable? = null

    private var mInProgress = false


    override fun detach() {
        mListener = null
        mLocationAwaitSubscription?.dispose()
    }

    override fun isInProgress() = mInProgress

    private fun notifyResult(location: Location?) {
        mInProgress = false
        mLocationAwaitSubscription?.dispose()
        mListener?.onLocationObtained(location)

    }

    override fun getCurrentLocation() {
        mListener?.showProgress()
        mInProgress = true

        val view = mListener ?: run {
            notifyResult(null)
            return
        }

        mPermissionAwaitSubscription = mPermissionAwaitObservable.subscribe { granted ->
            mPermissionAwaitSubscription?.dispose()
            
            if (!granted) {
                notifyResult(null)
                return@subscribe
            }

            // Create locator and await for location
            val locator = SmartLocation.with(app()).location()
            mLocationAwaitSubscription = ObservableFactory.from(locator).subscribe { location ->
                locator.stop()
                notifyResult(location)
            }
        }

        if (!view.requestLocationPermissions())
            onLocationPermissionsGrantResult(true)
    }

    override fun onLocationPermissionsGrantResult(granted: Boolean) {
        mPermissionAwaitObservable.onNext(granted)
    }
}
