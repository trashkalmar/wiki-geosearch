package com.pocketimps.samples.geosearchwiki.util

import android.view.View

fun View.showIf(show: Boolean = true) {
    visibility = (if (show) View.VISIBLE else View.GONE)
}

fun View.visibleIf(show: Boolean = true) {
    visibility = (if (show) View.VISIBLE else View.INVISIBLE)
}

fun View.show() = showIf(true)
fun View.hide() = showIf(false)
