package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.Paper
import com.example.myapplication.data.model.ScientificField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetDiscoverFeedUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    private val allPapers = listOf(
        Paper(
            id = "p1",
            title = "Hawking Radiation and Black Hole Thermodynamics",
            authors = "Stephen Hawking et al.",
            field = ScientificField.PHYSICS,
            summary = "Exploring the quantum properties of black holes and their thermal evaporation."
        ),
        Paper(
            id = "p2",
            title = "Quantum Superposition and Schrödinger's Cat",
            authors = "Erwin Schrödinger, John Bell",
            field = ScientificField.PHYSICS,
            summary = "A deep dive into quantum mechanics and the paradox of superposition states."
        ),
        Paper(
            id = "p3",
            title = "CRISPR Gene Editing and Genetic Disorders",
            authors = "Jennifer Doudna, Emmanuelle Charpentier",
            field = ScientificField.BIOLOGY,
            summary = "Revolutionary gene-editing technology and its implications for treating genetic diseases."
        ),
        Paper(
            id = "p4",
            title = "Ocean Currents and Climate Regulation",
            authors = "Jacques Cousteau, Carl Wunsch",
            field = ScientificField.EARTH_SCIENCE,
            summary = "Understanding how ocean circulation patterns influence global climate patterns."
        ),
        Paper(
            id = "p5",
            title = "Neural Processing Speed and Brain Plasticity",
            authors = "Donald Hebb, Sandra Romain",
            field = ScientificField.NEUROSCIENCE,
            summary = "How the brain processes visual information and adapts to experience."
        ),
        Paper(
            id = "p6",
            title = "Chemical Reactions and Molecular Interactions",
            authors = "Linus Pauling, Dorothy Hodgkin",
            field = ScientificField.CHEMISTRY,
            summary = "Fundamental principles of chemical bonding and reaction kinetics."
        ),
        Paper(
            id = "p7",
            title = "Machine Learning and Neural Networks",
            authors = "Geoffrey Hinton, Yann LeCun",
            field = ScientificField.COMPUTER_SCIENCE,
            summary = "Deep learning architectures and their applications in AI systems."
        ),
        Paper(
            id = "p8",
            title = "DNA Structure and Genetic Code",
            authors = "Francis Crick, Rosalind Franklin",
            field = ScientificField.BIOLOGY,
            summary = "The molecular basis of heredity and genetic information transfer."
        ),
        Paper(
            id = "p9",
            title = "Particle Physics and the Standard Model",
            authors = "Murray Gell-Mann, Peter Higgs",
            field = ScientificField.PHYSICS,
            summary = "Understanding fundamental particles and their interactions."
        ),
        Paper(
            id = "p10",
            title = "Climate Change and Atmospheric Chemistry",
            authors = "Paul Crutzen, Mario Molina",
            field = ScientificField.EARTH_SCIENCE,
            summary = "The role of chemistry in ozone depletion and climate change."
        )
    )

    suspend operator fun invoke(pageIndex: Int, category: ScientificField? = null): List<Paper> =
        withContext(ioDispatcher) {
            val filtered = if (category != null) {
                allPapers.filter { it.field == category }
            } else {
                allPapers
            }

            val pageSize = 3
            val startIndex = pageIndex * pageSize
            val endIndex = minOf(startIndex + pageSize, filtered.size)

            if (startIndex >= filtered.size) emptyList() else filtered.subList(startIndex, endIndex)
        }
}
