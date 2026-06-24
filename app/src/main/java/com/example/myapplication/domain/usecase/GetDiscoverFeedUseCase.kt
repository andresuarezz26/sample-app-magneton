package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetDiscoverFeedUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    companion object {
        const val PAGE_SIZE = 4
    }

    suspend operator fun invoke(category: String? = null, page: Int = 0): List<Paper> = withContext(ioDispatcher) {
        val allPapers = listOf(
            Paper(
                id = "p1",
                title = "Quantum Entanglement: The EPR Paradox Revisited",
                authors = listOf("Alice Smith", "Bob Johnson"),
                field = "Physics",
                abstract = "This paper re-examines the famous EPR paradox and its implications for quantum mechanics.",
                likes = 234
            ),
            Paper(
                id = "p2",
                title = "CRISPR Gene Editing: Advances and Challenges",
                authors = listOf("Carol White", "David Brown"),
                field = "Biology",
                abstract = "A comprehensive review of CRISPR technology and its applications in genetic medicine.",
                likes = 567
            ),
            Paper(
                id = "p3",
                title = "Machine Learning Algorithms for Cancer Detection",
                authors = listOf("Eve Davis", "Frank Wilson"),
                field = "CS",
                abstract = "Deep learning models for early detection of cancer from medical imaging data.",
                likes = 892
            ),
            Paper(
                id = "p4",
                title = "Graphene Properties and Industrial Applications",
                authors = listOf("Grace Lee", "Henry Chen"),
                field = "Chemistry",
                abstract = "Exploring the unique properties of graphene and its potential commercial applications.",
                likes = 345
            ),
            Paper(
                id = "p5",
                title = "Climate Change Impact on Ocean Acidification",
                authors = listOf("Iris Martinez", "Jack Taylor"),
                field = "Biology",
                abstract = "Analyzing the effects of rising CO2 levels on marine ecosystems and biodiversity.",
                likes = 678
            ),
            Paper(
                id = "p6",
                title = "Superconductivity and Room Temperature Conductors",
                authors = listOf("Kate Anderson", "Leo Brown"),
                field = "Physics",
                abstract = "Recent breakthroughs in achieving superconductivity at near-room temperatures.",
                likes = 445
            ),
            Paper(
                id = "p7",
                title = "Neural Networks and Artificial General Intelligence",
                authors = listOf("Maya Patel", "Nathan Scott"),
                field = "CS",
                abstract = "Investigating the path toward AGI through advances in neural network architectures.",
                likes = 1023
            ),
            Paper(
                id = "p8",
                title = "Photosynthesis Optimization in Crop Plants",
                authors = listOf("Olivia Green", "Peter White"),
                field = "Biology",
                abstract = "Methods to enhance photosynthetic efficiency for improved agricultural yields.",
                likes = 234
            ),
            Paper(
                id = "p9",
                title = "Quantum Computing: From Theory to Practice",
                authors = listOf("Quinn Roberts", "Rachel King"),
                field = "CS",
                abstract = "A survey of quantum computing algorithms and their practical implementations.",
                likes = 756
            ),
            Paper(
                id = "p10",
                title = "Black Hole Thermodynamics Revisited",
                authors = listOf("Sam Jackson", "Tina Harris"),
                field = "Physics",
                abstract = "New insights into black hole entropy and Hawking radiation mechanisms.",
                likes = 567
            ),
            Paper(
                id = "p11",
                title = "Polymer Chemistry: New Materials for Sustainable Technology",
                authors = listOf("Uma Sharma", "Victor Lopez"),
                field = "Chemistry",
                abstract = "Development of biodegradable polymers for environmental sustainability.",
                likes = 389
            ),
            Paper(
                id = "p12",
                title = "Neuroscience: Understanding Memory Formation",
                authors = listOf("Wendy Moore", "Xavier Cross"),
                field = "Biology",
                abstract = "Mechanisms of synaptic plasticity and memory consolidation in the brain.",
                likes = 634
            )
        )

        val filtered = if (category != null && category != "All") {
            allPapers.filter { it.field == category }
        } else {
            allPapers
        }

        val startIndex = page * PAGE_SIZE
        val endIndex = minOf(startIndex + PAGE_SIZE, filtered.size)

        if (startIndex >= filtered.size) {
            emptyList()
        } else {
            filtered.subList(startIndex, endIndex)
        }
    }
}
