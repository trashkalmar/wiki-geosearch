package com.pocketimps.samples.geosearchwiki.ui.controller

import android.location.Location
import android.util.SparseArray
import com.pocketimps.samples.geosearchwiki.app.NetworkApi
import com.pocketimps.samples.geosearchwiki.data.model.GeoSearchResult
import com.pocketimps.samples.geosearchwiki.data.model.GeoSearchResultList
import com.pocketimps.samples.geosearchwiki.data.model.raw.RawGeoSearchResultEnvelope
import com.pocketimps.samples.geosearchwiki.data.model.raw.RawPageImagesResultEnvelope
import com.pocketimps.samples.geosearchwiki.util.Optional
import com.pocketimps.samples.geosearchwiki.util.UiThread
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

class DataLoadControllerImpl @Inject constructor(listener: DataLoadListener)
    : DataLoadController {
    @Inject
    lateinit var api: NetworkApi

    private var mListener: DataLoadListener? = listener
    private var mCurrentRequest: Disposable? = null

    private var mInProgress = false
    private val mResults = SparseArray<GeoSearchResult>()

    override fun isInProgress() = mInProgress

    private fun finishQuery() {
        mInProgress = false

        mCurrentRequest?.dispose()
        mCurrentRequest = null

        mResults.clear()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onLoadError(error: Throwable) = UiThread.runUi {
        mListener?.onError()
        finishQuery()
    }

    private fun onLoadFinished() = UiThread.runUi {
        // Flush list from sparse array
        val res = GeoSearchResultList()
        0.until(mResults.size()).forEach {
            res.add(mResults.valueAt(it))
        }

        mListener?.setResults(res)
        finishQuery()
    }

    private fun getImagesInitial(envelope: RawGeoSearchResultEnvelope): Observable<RawPageImagesResultEnvelope>? {
        return envelope.content?.getPages()?.let { pages ->
            getNextImages(pages, null, null)
        }
    }

    private fun getNextImages(pages: String, cblock1: String?, cblock2: String?): Observable<RawPageImagesResultEnvelope> {
        return api.getImages(pages, cblock1, cblock2)
                  .concatMap { envelope ->
                      var res = Observable.just(envelope)

                      envelope.continuation?.let {
                          // Recursively query next page
                          res = res.concatWith(getNextImages(pages, it.param1, it.param2))
                      }

                      res
                }
    }

    private fun getImageListFromRaw(envelope: RawPageImagesResultEnvelope)
            = Optional(GeoSearchResultList.from(envelope))

    private fun appendResults(newItems: Optional<GeoSearchResultList>) {
        newItems.value?.forEach { item ->
            var existing = mResults.get(item.pageId)
            if (existing == null) {
                mResults.put(item.pageId, item)
                existing = item
            }

            existing.images.addAll(item.images)
        }
    }

    override fun loadArticles(location: Location?) {
        val view = mListener ?: return
        mInProgress = true
        mResults.clear()

        val loc = location ?: run {
            // Failed to obtain location. Let it be RANDOM! :)
            val random = Random()

            Location("Random").apply {
                // [0.0 … 50.0)
                latitude = random.nextDouble() * 50.0

                // [45.0 … 60.0)
                longitude = random.nextDouble() * 15.0 + 45.0
            }
        }

        view.showProgress()

        // Fetch paginated data from wiki:
        // 1. Perform geosearch by given location.
        // 2. Join returned page ID list (164|722|8363|3361…)
        // 3. Query images for this page list.
        // 4. Convert list from raw to inner model and append to resulting list.
        // 5. Repeat step 3 while continuation block in resulting JSON is not empty.

        mCurrentRequest = api.geoSearch("${loc.latitude}|${loc.longitude}")
                             .concatMap(this::getImagesInitial)
                             .map(this::getImageListFromRaw)
                             .subscribe(::appendResults,
                                        ::onLoadError,
                                        ::onLoadFinished)
    }

    override fun detach() {
        mListener = null

        mCurrentRequest?.dispose()
        mCurrentRequest = null

        mInProgress = false
    }
}
