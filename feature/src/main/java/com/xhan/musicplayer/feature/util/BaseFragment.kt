package com.xhan.musicplayer.feature.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.xhan.musicplayer.feature.R

abstract class BaseDataBindingFragment<VB : ViewDataBinding, VM : ViewModel> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: VM

    protected abstract fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun showDialog(
        @StringRes titleRes: Int,
        @StringRes messageRes: Int,
        @StringRes positiveButtonRes: Int = R.string.error_dialog_confirm,
        onDismiss: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(titleRes)
            .setMessage(messageRes)
            .setPositiveButton(positiveButtonRes) { dialog, _ ->
                dialog.dismiss()
                onDismiss?.invoke()
            }
            .setCancelable(false)
            .show()
    }
}