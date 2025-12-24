package com.xhan.musicplayer.feature.list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.xhan.musicplayer.feature.R
import com.xhan.musicplayer.feature.util.showSnackbar

class PermissionHelper(
    private val fragment: Fragment,
    private val onPermissionGranted: () -> Unit,
    private val onPermissionDenied: () -> Unit
) {

    private val permissionLauncher: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) onPermissionGranted()
            else onPermissionDenied()
        }

    fun checkAndRequestPermission() {
        when {
            hasPermission() -> onPermissionGranted()
            fragment.shouldShowRequestPermissionRationale(getRequiredPermission()) -> showPermissionRationale()
            else -> requestPermission()
        }
    }

    private fun hasPermission(): Boolean {
        val permission = getRequiredPermission()
        return ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getRequiredPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO
        else Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private fun requestPermission() {
        permissionLauncher.launch(getRequiredPermission())
    }

    private fun showPermissionRationale() {
        fragment.view?.showSnackbar(
            messageRes = R.string.permission_rationale_message,
            duration = Snackbar.LENGTH_LONG,
            actionTextRes = R.string.permission_allow_action,
            action = { requestPermission() }
        )
    }
}