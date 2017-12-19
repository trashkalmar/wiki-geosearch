package com.pocketimps.samples.geosearchwiki.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.OrientationHelper
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.pocketimps.samples.R

/**
 * Widget implementing collapse/expand behavior.
 */
class ExpandableLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    private var mDuration = DEFAULT_DURATION

    private var mParallax = 0.0f
        set(value) {
            field = Math.min(1.0f, Math.max(0.0f, value))
        }

    private var mIsVertical = true
    private var mAnimator: ValueAnimator? = null

    var expansion = EXPANSION_COLLAPSED
        private set

    var state = STATE_COLLAPSED
        private set

    var listener: OnExpansionUpdateListener? = null
    var interpolator = FastOutSlowInInterpolator()

    interface OnExpansionUpdateListener {
        /**
         * @param fraction Value between 0 (collapsed) and 1 (expanded) representing the the expansion progress
         * @param state    One of STATE_… repesenting the current expansion state
         */
        fun onExpansionUpdate(fraction: Float, state: Int)
    }


    private inner class ExpansionListener(private val targetExpansion: Float) : AnimatorListenerAdapter() {
        private var mCanceled = false

        override fun onAnimationStart(animation: Animator) {
            state = if (targetExpansion.isCollapsed()) STATE_COLLAPSING
                                                  else STATE_EXPANDING
        }

        override fun onAnimationEnd(animation: Animator) {
            if (!mCanceled) {
                state = if (targetExpansion.isCollapsed()) STATE_COLLAPSED
                                                      else STATE_EXPANDED
                expandTo(targetExpansion)
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            mCanceled = true
        }
    }


    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout)
            mDuration = a.getInt(R.styleable.ExpandableLayout_el_duration, DEFAULT_DURATION)
            expansion = a.getBoolean(R.styleable.ExpandableLayout_el_expanded, false).asExpansion()
            mIsVertical = a.getInt(R.styleable.ExpandableLayout_android_orientation, OrientationHelper.VERTICAL) == OrientationHelper.VERTICAL
            mParallax = a.getFloat(R.styleable.ExpandableLayout_el_parallax, 1.0f)
            a.recycle()
        }

        state = if (expansion.isCollapsed()) STATE_COLLAPSED
                                        else STATE_EXPANDED
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        expansion = isExpanded().asExpansion()
        return Bundle().apply {
            putFloat(KEY_EXPANSION, expansion)
            putParcelable(KEY_SUPER_STATE, superState)
        }
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val bundle = parcelable as Bundle
        expansion = bundle.getFloat(KEY_EXPANSION)
        state = if (expansion == EXPANSION_EXPANDED) STATE_EXPANDED
                                                else STATE_COLLAPSED

        val superState = bundle.getParcelable<Parcelable>(KEY_SUPER_STATE)
        super.onRestoreInstanceState(superState)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        val height = measuredHeight

        val size = if (mIsVertical) height else width
        showIf(!expansion.isCollapsed() || size != 0)

        val expansionDelta = size - Math.round(size * expansion)
        if (mParallax > 0) {
            val parallaxDelta = expansionDelta * mParallax
            (0 until childCount).map(this::getChildAt)
                                .forEach {
                                    if (mIsVertical) {
                                        it.translationY = -parallaxDelta
                                    } else {
                                        val direction = if (layoutDirection == View.LAYOUT_DIRECTION_RTL) 1 else -1
                                        it.translationX = direction * parallaxDelta
                                    }
                                }
        }

        if (mIsVertical)
            setMeasuredDimension(width, height - expansionDelta)
        else
            setMeasuredDimension(width - expansionDelta, height)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        mAnimator?.cancel()
        super.onConfigurationChanged(newConfig)
    }

    fun toggle(animate: Boolean = true) = setExpanded(!isExpanded(), animate)

    private fun isExpanded() = (state == STATE_EXPANDING || state == STATE_EXPANDED)

    fun setExpanded(expand: Boolean, animate: Boolean) {
        if (expand == isExpanded())
            return

        val targetExpansion = expand.asExpansion()
        if (animate)
            animateSize(targetExpansion)
        else
            expandTo(targetExpansion)
    }

    private fun expandTo(newExpansion: Float) {
        if (expansion == newExpansion)
            return

        // Infer state from previous value
        val delta = newExpansion - expansion
        state = when {
            newExpansion.isCollapsed() -> STATE_COLLAPSED
            newExpansion.isExpanded() -> STATE_EXPANDED
            delta < 0 -> STATE_COLLAPSING
            delta > 0 -> STATE_EXPANDING
            else -> state
        }

        showIf(state != STATE_COLLAPSED)
        expansion = newExpansion
        requestLayout()

        listener?.onExpansionUpdate(newExpansion, state)
    }

    private fun animateSize(targetExpansion: Float) {
        mAnimator?.let {
            it.cancel()
            mAnimator = null
        }

        mAnimator = ValueAnimator.ofFloat(expansion, targetExpansion).apply {
            interpolator = this@ExpandableLayout.interpolator
            duration = mDuration.toLong()

            addUpdateListener { valueAnimator ->
                expandTo(valueAnimator.animatedValue as Float)
            }

            addListener(ExpansionListener(targetExpansion))
            start()
        }
    }


    companion object {
        private const val DEFAULT_DURATION = 300

        private const val EXPANSION_COLLAPSED = 0.0f
        private const val EXPANSION_EXPANDED = 1.0f

        private const val KEY_SUPER_STATE = "super_state"
        private const val KEY_EXPANSION = "expansion"

        const val STATE_COLLAPSED = 0
        const val STATE_COLLAPSING = 1
        const val STATE_EXPANDING = 2
        const val STATE_EXPANDED = 3


        private fun Float.isCollapsed() = (this == EXPANSION_COLLAPSED)
        private fun Float.isExpanded() = (this == EXPANSION_EXPANDED)
        private fun Boolean.asExpansion() = if (this) EXPANSION_EXPANDED else EXPANSION_COLLAPSED
    }
}
