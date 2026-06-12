package com.example.myapplication.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        onIntent(HomeIntent.LoadFeed)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadFeed -> loadFeed()
            is HomeIntent.LikeVideo -> likeVideo(intent.videoId)
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            _state.update { it.copy(videos = mockVideoFeed(), isLoading = false) }
        }
    }

    private fun likeVideo(videoId: String) {
        _state.update { current ->
            current.copy(
                videos = current.videos.map { video ->
                    if (video.id == videoId) video.copy(likes = video.likes + 1) else video
                }
            )
        }
    }
}

private fun mockVideoFeed(): List<VideoItem> = listOf(
    VideoItem(
        id = "1",
        author = "@astro_facts",
        description = "Did you know black holes emit Hawking radiation? The universe is wild! #space #physics",
        likes = 48200,
        comments = 1203,
        shares = 892,
        music = "Cosmic Vibes — Science Beats",
        backgroundColorHex = 0xFF1A1A2E
    ),
    VideoItem(
        id = "2",
        author = "@quantum_lab",
        description = "Schrodinger's cat explained in 60 seconds. Mind = blown. #quantum #physics",
        likes = 92100,
        comments = 4521,
        shares = 3100,
        music = "Wave Function — Quantum Sounds",
        backgroundColorHex = 0xFF16213E
    ),
    VideoItem(
        id = "3",
        author = "@bio_wonders",
        description = "CRISPR gene editing could cure genetic diseases forever. The future of medicine is here! #biology",
        likes = 73400,
        comments = 2890,
        shares = 5601,
        music = "DNA Sequence — Bio Beats",
        backgroundColorHex = 0xFF0F3460
    ),
    VideoItem(
        id = "4",
        author = "@climate_science",
        description = "Ocean currents regulate our entire climate. Here's how they work and why they matter. #earth",
        likes = 31500,
        comments = 987,
        shares = 2341,
        music = "Ocean Drift — Nature Sounds",
        backgroundColorHex = 0xFF1B4332
    ),
    VideoItem(
        id = "5",
        author = "@neuro_brain",
        description = "Your brain processes images in just 13 milliseconds. Faster than you can blink. #neuroscience",
        likes = 115000,
        comments = 6200,
        shares = 8900,
        music = "Neural Pulse — Brain Beats",
        backgroundColorHex = 0xFF3D0C11
    ),
    VideoItem(
        id = "6",
        author = "@chem_lab",
        description = "Why does mixing bleach and ammonia create a deadly gas? The chemistry explained safely. #chemistry",
        likes = 204000,
        comments = 9800,
        shares = 14200,
        music = "Molecule Mix — Lab Sounds",
        backgroundColorHex = 0xFF2D3561
    )
)
