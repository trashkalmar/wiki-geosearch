package com.pocketimps.samples.geosearchwiki.data.model.raw

import com.google.gson.annotations.SerializedName

/**
 * Outer container for query results.
 * Each resulting JSON looks like:
 * ```
 * {
 *   "continue": {
 *     "imcontinue": "continuation1",
 *     "continue": "continuation2"
 *   },
 *   "query": {
 *     …
 *   }
 * }
 * ```
 */
class RawQueryEnvelope<out C>(@SerializedName("query") val content: C?,
                              @SerializedName("continue") val continuation: RawContinuationBlock?)
