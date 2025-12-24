package com.xhan.musicplayer.feature.util

import android.view.View
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