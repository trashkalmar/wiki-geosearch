package com.pocketimps.samples.geosearchwiki.data.model.raw

import com.google.gson.annotations.SerializedName

class RawContinuationBlock(@SerializedName("imcontinue") val param1: String?,
                           @SerializedName("continue") val param2: String?)
