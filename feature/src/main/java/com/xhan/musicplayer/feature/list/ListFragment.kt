package com.xhan.musicplayer.feature.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xhan.musicplayer.feature.R
import com.xhan.musicplayer.feature.databinding.FragmentListBinding
import com.xhan.musicplayer.feature.util.BaseDataBindingFragment
import com.xhan.musicplayer.feature.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : BaseDataBindingFragment<FragmentListBinding, ListViewModel>() {

    override val viewModel: ListViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListBinding = FragmentListBinding.inflate(inflater, container, false)

    private lateinit var trackAdapter: TrackAdapter

    private lateinit var permissionHelper: PermissionHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupPermissionHelper()
        permissionHelper.checkAndRequestPermission()
    }

    private fun setupPermissionHelper() {
        permissionHelper = PermissionHelper(
            fragment = this,
            onPermissionGranted = { observeUiState() },
            onPermissionDenied = { showPermissionDeniedMessage() }
        )
    }

    private fun showPermissionDeniedMessage() {
        binding.root.showSnackbar(
            messageRes = R.string.permission_denied_message,
            duration = Snackbar.LENGTH_LONG
        )
        binding.emptyText.isVisible = true
        binding.emptyText.setText(R.string.permission_required_message)
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter { track ->
            viewModel.onTrackClick(track)
            findNavController().navigate(R.id.action_list_to_detail)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    updateUi(uiState)
                }
            }
        }
    }

    private fun updateUi(uiState: ListUiState) {
        binding.uiState = uiState

        trackAdapter.submitList(uiState.tracks)
        trackAdapter.setCurrentPlayingTrack(uiState.currentPlayingTrack?.id)
    }
}