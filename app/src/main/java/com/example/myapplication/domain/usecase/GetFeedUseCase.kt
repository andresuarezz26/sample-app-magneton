package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

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
                backgroundColorHex = 0xFF1A1A2E,
                paperTitle = "Hawking Radiation and Black Hole Thermodynamics",
                paperAuthors = "S. Hawking, J. Bekenstein",
                paperAbstract = "Black holes are not completely black. Due to quantum effects, they emit radiation with a thermal spectrum. This fundamental discovery connects general relativity, quantum mechanics, and thermodynamics.",
                fieldTag = "Astrophysics",
                paperUrl = "https://example.com/paper1",
                videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4"
            ),
            VideoItem(
                id = "2",
                author = "@quantum_lab",
                description = "Schrodinger's cat explained in 60 seconds. Mind = blown. #quantum #physics",
                likes = 92100,
                comments = 4521,
                shares = 3100,
                music = "Wave Function — Quantum Sounds",
                backgroundColorHex = 0xFF16213E,
                paperTitle = "The Present Situation in Quantum Mechanics",
                paperAuthors = "E. Schrodinger",
                paperAbstract = "A thought experiment describing a cat that is simultaneously alive and dead until observed. This paradox illustrates the measurement problem in quantum mechanics and the role of observation.",
                fieldTag = "Quantum Physics",
                paperUrl = "https://example.com/paper2",
                videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4"
            ),
            VideoItem(
                id = "3",
                author = "@bio_wonders",
                description = "CRISPR gene editing could cure genetic diseases forever. The future of medicine is here! #biology",
                likes = 73400,
                comments = 2890,
                shares = 5601,
                music = "DNA Sequence — Bio Beats",
                backgroundColorHex = 0xFF0F3460,
                paperTitle = "CRISPR-Cas9 Gene Editing: Applications and Challenges",
                paperAuthors = "J. Doudna, E. Sontheimer",
                paperAbstract = "CRISPR-Cas9 is a revolutionary gene-editing tool that allows precise modifications of DNA. This review covers its mechanism, therapeutic applications, and current challenges in clinical translation.",
                fieldTag = "Molecular Biology",
                paperUrl = "https://example.com/paper3",
                videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4"
            ),
            VideoItem(
                id = "4",
                author = "@climate_science",
                description = "Ocean currents regulate our entire climate. Here's how they work and why they matter. #earth",
                likes = 31500,
                comments = 987,
                shares = 2341,
                music = "Ocean Drift — Nature Sounds",
                backgroundColorHex = 0xFF1B4332,
                paperTitle = "Ocean Circulation and Climate Regulation",
                paperAuthors = "W. Broecker, T. Stocker",
                paperAbstract = "Ocean currents transport heat across the globe and play a critical role in regulating Earth's climate. Understanding thermohaline circulation is essential for climate prediction models.",
                fieldTag = "Oceanography",
                paperUrl = "https://example.com/paper4",
                videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4"
            ),
            VideoItem(
                id = "5",
                author = "@neuro_brain",
                description = "Your brain processes images in just 13 milliseconds. Faster than you can blink. #neuroscience",
                likes = 115000,
                comments = 6200,
                shares = 8900,
                music = "Neural Pulse — Brain Beats",
                backgroundColorHex = 0xFF3D0C11,
                paperTitle = "Rapid Object Recognition in the Human Brain",
                paperAuthors = "M. Bar, K. Tootell",
                paperAbstract = "The human visual system can recognize objects in as little as 13 milliseconds, faster than conscious awareness. This rapid processing involves parallel pathways and hierarchical feature detection.",
                fieldTag = "Neuroscience",
                paperUrl = "https://example.com/paper5",
                videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4"
            ),
            VideoItem(
                id = "6",
                author = "@chem_lab",
                description = "Why does mixing bleach and ammonia create a deadly gas? The chemistry explained safely. #chemistry",
                likes = 204000,
                comments = 9800,
                shares = 14200,
                music = "Molecule Mix — Lab Sounds",
                backgroundColorHex = 0xFF2D3561,
                paperTitle = "Chemical Reactions Between Household Disinfectants",
                paperAuthors = "H. Chen, P. Smith",
                paperAbstract = "The reaction between bleach and ammonia produces toxic chloramine gas. Understanding chemical compatibility of household products is crucial for safety and preventing accidental poisonings.",
                fieldTag = "Chemistry",
                paperUrl = "https://example.com/paper6",
                videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4"
            )
        )
    }
}
