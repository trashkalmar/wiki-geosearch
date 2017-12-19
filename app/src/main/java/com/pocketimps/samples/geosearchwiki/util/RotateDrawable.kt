package com.pocketimps.samples.geosearchwiki.util

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Rect
import android.graphics.drawable.Drawable

/**
 * Drawable with rotation control.
 */
class RotateDrawable(private val mBaseDrawable: Drawable) : Drawable() {
    private var mAngle = 0.0f


    override fun setAlpha(alpha: Int) {
        mBaseDrawable.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mBaseDrawable.colorFilter = cf
    }

    override fun getOpacity() = mBaseDrawable.opacity

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mBaseDrawable.setBounds(0, 0, mBaseDrawable.intrinsicWidth,
                                mBaseDrawable.intrinsicHeight)
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.rotate(mAngle, bounds.width() * 0.5f, bounds.height() * 0.5f)
        canvas.translate((bounds.width() - mBaseDrawable.intrinsicWidth) * 0.5f,
                         (bounds.height() - mBaseDrawable.intrinsicHeight) * 0.5f)
        mBaseDrawable.draw(canvas)
        canvas.restore()
    }

    fun setAngle(angle: Float) {
        mAngle = angle
        invalidateSelf()
    }
}
