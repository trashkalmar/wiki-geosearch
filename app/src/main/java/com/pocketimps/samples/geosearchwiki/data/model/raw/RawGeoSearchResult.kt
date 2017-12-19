package com.pocketimps.samples.geosearchwiki.data.model.raw

import com.google.gson.annotations.SerializedName

/**
 * Resulting JSON has following structure:
 * ```
 * {
 *   "query": {
 *     "geosearch": [
 *       {
 *         "pageid": 123456,
 *         "title": "Title",
 *       }, …
 *     ]
 *   }
 * }
 * ```
 */
typealias RawGeoSearchResultEnvelope = RawQueryEnvelope<RawGeoSearchResultInnerEnvelope>

class RawGeoSearchResultInnerEnvelope(@SerializedName("geosearch") private val mResults: List<RawGeoSearchResult?>?) {
    /**
     * @return Pipe-separated list of resulting pages (234|7442|999).
     */
    fun getPages(): String {
        val sb = StringBuilder()

        mResults?.forEach { it ->
            it?.pageId?.let {
                if (!sb.isEmpty())
                    sb.append('|')

                sb.append(it)
            }
        }

        return sb.toString()
    }
}

class RawGeoSearchResult(@SerializedName("pageid") val pageId: Int?)
