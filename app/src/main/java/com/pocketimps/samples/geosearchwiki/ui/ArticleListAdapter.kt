package com.pocketimps.samples.geosearchwiki.ui

import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.pocketimps.samples.R
import com.pocketimps.samples.geosearchwiki.app.App.Companion.app
import com.pocketimps.samples.geosearchwiki.data.model.GeoSearchResult
import com.pocketimps.samples.geosearchwiki.data.model.GeoSearchResultList
import com.pocketimps.samples.geosearchwiki.util.ExpandableLayout
import com.pocketimps.samples.geosearchwiki.util.RotateDrawable
import com.pocketimps.samples.geosearchwiki.util.visibleIf

class ArticleListAdapter : RecyclerView.Adapter<ArticleListAdapter.ViewHolder>() {
    private val mItems = GeoSearchResultList()


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mGroupFrame = view.findViewById<View>(R.id.group_frame)
        private val mGroupTitle = view.findViewById<TextView>(R.id.article_title)
        private val mGroupCounter = view.findViewById<TextView>(R.id.images_count)
        private val mExpandFrame = view.findViewById<ExpandableLayout>(R.id.expand_frame)
        private val mImagesFrame = view.findViewById<ViewGroup>(R.id.images_frame)

        private val mChevron = view.findViewById<ImageView>(R.id.chevron)
        private val mChevronDrawable = RotateDrawable(AppCompatResources.getDrawable(itemView.context, R.drawable.vector_chevron)!!)

        private lateinit var mArticle: GeoSearchResult

        private inner class ItemViewHolder(frame: View) {
            private val mTitle = frame as TextView

            fun update(item: String) {
                mTitle.text = item
            }
        }


        init {
            mChevron.setImageDrawable(mChevronDrawable)

            mGroupFrame.setOnClickListener {
                mArticle.expanded = !mArticle.expanded
                mExpandFrame.toggle()

                if (mArticle.expanded)
                    updateItemFrameView()
            }

            mExpandFrame.listener = object : ExpandableLayout.OnExpansionUpdateListener {
                // Sync chevron rotation with expanding state
                override fun onExpansionUpdate(fraction: Float, state: Int) = updateChevron()
            }
        }

        private fun createItemView(): View {
            val res = LayoutInflater.from(itemView.context).inflate(R.layout.view_item, mImagesFrame, false)
            mImagesFrame.addView(res)
            return res
        }

        private fun updateChevron() {
            val show = mArticle.images.isNotEmpty()
            mChevron.visibleIf(show)

            if (show)
                mChevronDrawable.setAngle(90.0f * mExpandFrame.expansion)
        }

        private fun updateGroupView() {
            mExpandFrame.setExpanded(mArticle.expanded, false)
            updateChevron()
            mGroupTitle.text = mArticle.title

            val text = if (mArticle.images.isEmpty()) app().getString(R.string.article_name_no_images)
                                                 else app().resources.getQuantityString(R.plurals.article_name_count_template,
                                                                                        mArticle.images.size, mArticle.images.size)
            mGroupCounter.text = text
        }

        private fun updateItemView(view: View, item: String) {
            var holder = view.tag as? ItemViewHolder
            if (holder == null) {
                holder = ItemViewHolder(view)
                view.tag = holder
            }

            holder.update(item)
        }

        private fun updateItemFrameView() {
            // List of images is inflated if the group is explicitly expanded by user.
            // Until this moment the frame is empty to speed-up inflation and save resources.

            if (!mArticle.expanded) {
                mImagesFrame.removeAllViews()
                return
            }
            
            mArticle.images.forEachIndexed { i, item ->
                val view = if (i >= mImagesFrame.childCount)
                    createItemView()
                else
                    mImagesFrame.getChildAt(i)

                updateItemView(view, item)
            }

            val toRemove = mImagesFrame.childCount - mArticle.images.size
            if (toRemove > 0)
                mImagesFrame.removeViews(mArticle.images.size, toRemove)
        }

        fun bind(article: GeoSearchResult) {
            mArticle = article
            updateGroupView()
            updateItemFrameView()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item_group, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getItemCount() = mItems.size

    fun setItems(items: GeoSearchResultList) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }
}
