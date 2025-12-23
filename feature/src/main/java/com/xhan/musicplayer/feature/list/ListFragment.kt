package com.xhan.musicplayer.feature.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xhan.musicplayer.feature.databinding.FragmentListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by viewModels()

    private lateinit var trackAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter { track ->
            viewModel.onTrackClick(track)
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
        // 로딩 상태
        binding.progressBar.isVisible = uiState.isLoading

        // 트랙 목록
        val hasTrack = uiState.tracks.isNotEmpty()
        binding.recyclerView.isVisible = hasTrack && !uiState.isLoading
        binding.emptyText.isVisible = !hasTrack && !uiState.isLoading

        // 트랙 목록 업데이트
        trackAdapter.submitList(uiState.tracks)

        // 현재 재생 중인 트랙
        trackAdapter.setCurrentPlayingTrack(uiState.currentPlayingTrack?.id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}