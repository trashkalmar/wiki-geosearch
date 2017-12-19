package com.pocketimps.samples.geosearchwiki.app

import com.pocketimps.samples.geosearchwiki.data.model.raw.RawGeoSearchResultEnvelope
import com.pocketimps.samples.geosearchwiki.data.model.raw.RawPageImagesResultEnvelope
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApi {
    @GET("/w/api.php?action=query&list=geosearch&gsradius=10000&gslimit=50&format=json")
    fun geoSearch(@Query("gscoord") latlon: String): Observable<RawGeoSearchResultEnvelope>

    @GET("/w/api.php?action=query&prop=images&imlimit=max&format=json")
    fun getImages(@Query("pageids") pageIds: String,
                  @Query("imcontinue") continuation1: String? = null,
                  @Query("continue") continuation2: String? = null)
            : Observable<RawPageImagesResultEnvelope>
}
