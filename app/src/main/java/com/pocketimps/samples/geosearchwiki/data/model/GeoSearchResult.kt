package com.pocketimps.samples.geosearchwiki.data.model

import com.pocketimps.samples.R
import com.pocketimps.samples.geosearchwiki.app.App.Companion.app
import com.pocketimps.samples.geosearchwiki.data.model.raw.RawPageImage
import com.pocketimps.samples.geosearchwiki.data.model.raw.RawPageImagesResult

class GeoSearchResult private constructor(val pageId: Int,
                                          val title: String,
                                          val images: ArrayList<String>,
                                          var expanded: Boolean = false) {
    companion object {
        private val mImageNamePrefix by lazy {
            app().getString(R.string.article_image_prefix)
        }

        
        private fun extractImageTitle(image: RawPageImage?): String {
            val res = image?.title ?: return app().getString(R.string.article_image_no_name)

            return if (res.startsWith(mImageNamePrefix))
                res.substring(mImageNamePrefix.length)
            else
                res
        }

        fun from(raw: RawPageImagesResult?): GeoSearchResult? {
            val pageId = raw?.pageId ?: return null
            val title = raw.title ?: app().getString(R.string.article_no_name)
            val images = ArrayList<String>()

            raw.images?.forEach { image ->
                image?.let {
                    images.add(extractImageTitle(it))
                }
            }

            return GeoSearchResult(pageId, title, images)
        }
    }
}
