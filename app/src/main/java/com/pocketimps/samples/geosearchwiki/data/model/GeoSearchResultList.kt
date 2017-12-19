package com.pocketimps.samples.geosearchwiki.data.model

import com.pocketimps.samples.geosearchwiki.data.model.raw.RawPageImagesResultEnvelope

class GeoSearchResultList : ArrayList<GeoSearchResult>() {
    companion object {
        fun from(raw: RawPageImagesResultEnvelope?): GeoSearchResultList {
            val res = GeoSearchResultList()

            // Convert from raw to app model
            raw?.content?.pages?.forEach { (_, image) ->
                GeoSearchResult.from(image)?.let(res::add)
            }

            return res
        }
    }
}
