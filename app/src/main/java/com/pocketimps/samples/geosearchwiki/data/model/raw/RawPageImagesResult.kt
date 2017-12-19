package com.pocketimps.samples.geosearchwiki.data.model.raw

import com.google.gson.annotations.SerializedName

/**
 * Resulting JSON has following structure:
 * ```
 * {
 *   "query": {
 *     "pages": {
 *       "123456": {
 *         "pageid": 123456,
 *         "title": "Title",
 *         "images": [
 *           {
 *             "title": "File:Some file name"
 *           }, …
 *         ]
 *       }, …
 *     }
 *   }
 * }
 * ```
 */
typealias RawPageImagesResultEnvelope = RawQueryEnvelope<RawPageImagesResultInnerEnvelope>

class RawPageImagesResultInnerEnvelope(@SerializedName("pages") val pages: Map<String, RawPageImagesResult?>?)

class RawPageImagesResult(@SerializedName("pageid") val pageId: Int?,
                          @SerializedName("title") val title: String?,
                          @SerializedName("images") val images: List<RawPageImage?>?)

class RawPageImage(@SerializedName("title") val title: String?)
