package com.xhan.musicplayer.feature.util

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(this, message, duration)
    if (actionText != null && action != null) {
        snackbar.setAction(actionText) { action() }
    }

    val snackbarView = snackbar.view
    val params = snackbarView.layoutParams as? ViewGroup.MarginLayoutParams
    params?.let {
        val density = context.resources.displayMetrics.density
        val bottomMarginPx = (64 * density).toInt()
        it.bottomMargin = bottomMarginPx
        snackbarView.layoutParams = it
    }

    snackbar.show()
}

fun View.showSnackbar(
    @StringRes messageRes: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    @StringRes actionTextRes: Int? = null,
    action: (() -> Unit)? = null
) {
    val message = context.getString(messageRes)
    val actionText = actionTextRes?.let { context.getString(it) }
    showSnackbar(message, duration, actionText, action)
}