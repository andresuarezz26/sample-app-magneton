package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Returns the (mock) video feed for the home screen.
 *
 * @param ioDispatcher the IO-bound [CoroutineContext] the work runs on; defaults to
 * [Dispatchers.IO] and can be overridden in tests with a deterministic dispatcher.
 */
class GetFeedUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(): List<VideoItem> = withContext(ioDispatcher) {
        listOf(
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
    }
}
