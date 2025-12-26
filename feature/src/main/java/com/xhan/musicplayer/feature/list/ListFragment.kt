package com.xhan.musicplayer.feature.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xhan.musicplayer.feature.R
import com.xhan.musicplayer.feature.databinding.FragmentListBinding
import com.xhan.musicplayer.feature.detail.DetailViewModel
import com.xhan.musicplayer.feature.util.BaseAdapter
import com.xhan.musicplayer.feature.util.BaseDataBindingFragment
import com.xhan.musicplayer.feature.util.OnItemClick
import com.xhan.musicplayer.feature.util.autoCleared
import com.xhan.musicplayer.feature.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : BaseDataBindingFragment<FragmentListBinding, ListViewModel>() {

    override val viewModel: ListViewModel by viewModels()
    private val detailViewModel: DetailViewModel by activityViewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListBinding = FragmentListBinding.inflate(inflater, container, false)

    private val trackHelper by autoCleared {
        TrackHelper(
            onItem = OnItemClick { track, _, _ ->
                viewModel.onTrackClick(track)
                findNavController().navigate(R.id.action_list_to_detail)
                false
            }
        )
    }

    private val trackAdapter by autoCleared {
        BaseAdapter(helper = trackHelper)
    }

    private lateinit var permissionHelper: PermissionHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupPermissionHelper()
        setupMiniPlayer()
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
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupMiniPlayer() {
        binding.miniPlayer.bind(detailViewModel, viewLifecycleOwner)

        binding.miniPlayer.setOnExpandClickListener {
            findNavController().navigate(R.id.action_list_to_detail)
        }

        binding.miniPlayer.setOnPlayPauseClickListener {
            detailViewModel.onPlayPauseClick()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.tracks.collect { tracks ->
                        trackHelper.updateTracks(tracks)
                        trackAdapter.submitList(tracks)
                    }
                }
                launch {
                    viewModel.currentPlayingTrack.collect { track ->
                        trackHelper.setCurrentPlayingTrack(track?.id, trackAdapter)
                    }
                }
                launch {
                    viewModel.uiEvent.collect { event ->
                        handleUiEvent(event)
                    }
                }
            }
        }
    }

    private fun handleUiEvent(event: ListViewModel.Event) {
        when (event) {
            is ListViewModel.Event.ShowPermissionErrorDialog -> {
                showDialog(
                    titleRes = R.string.error_dialog_permission_title,
                    messageRes = R.string.error_dialog_permission_message
                )
            }

            is ListViewModel.Event.ShowInvalidDataErrorDialog -> {
                showDialog(
                    titleRes = R.string.error_dialog_invalid_data_title,
                    messageRes = R.string.error_dialog_invalid_data_message
                )
            }

            is ListViewModel.Event.ShowUnexpectedErrorDialog -> {
                showDialog(
                    titleRes = R.string.error_dialog_unexpected_title,
                    messageRes = R.string.error_dialog_unexpected_message
                )
            }
        }
    }
}