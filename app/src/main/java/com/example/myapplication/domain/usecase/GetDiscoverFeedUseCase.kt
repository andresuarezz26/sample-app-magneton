package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

data class DiscoverFeedPage(
    val papers: List<Paper>,
    val hasMore: Boolean
)

class GetDiscoverFeedUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    private val allPapers: List<Paper> = mockPapers()

    val categories: List<String> = allPapers.map { it.field }.distinct()

    suspend operator fun invoke(page: Int, pageSize: Int, category: String? = null): DiscoverFeedPage =
        withContext(ioDispatcher) {
            delay(300)
            val filtered = if (category == null) allPapers else allPapers.filter { it.field == category }
            val start = page * pageSize
            if (start >= filtered.size) {
                return@withContext DiscoverFeedPage(papers = emptyList(), hasMore = false)
            }
            val end = minOf(start + pageSize, filtered.size)
            DiscoverFeedPage(papers = filtered.subList(start, end), hasMore = end < filtered.size)
        }

    private fun mockPapers(): List<Paper> = listOf(
        Paper("1", "CRISPR-Cas9: Precision Gene Editing in Human Embryos", listOf("J. Zhang", "F. Doudna"), "Biology", 48200, 1203, 892, 0xFF1B4332),
        Paper("2", "Mapping the Human Microbiome's Role in Immunity", listOf("R. Alvarez", "K. Osei"), "Biology", 31200, 980, 640, 0xFF1B4332),
        Paper("3", "Photosynthesis Efficiency in Engineered Cyanobacteria", listOf("S. Iyer"), "Biology", 21400, 540, 310, 0xFF1B4332),
        Paper("4", "De Novo Protein Design Using Deep Learning", listOf("M. Baker", "L. Huang"), "Biology", 67300, 2100, 1890, 0xFF1B4332),
        Paper("5", "Coral Reef Resilience Under Thermal Stress", listOf("T. Nakamura"), "Biology", 15800, 410, 220, 0xFF1B4332),
        Paper("6", "Regenerative Capacity of Axolotl Limb Tissue", listOf("A. Petrova", "D. Osei"), "Biology", 28900, 760, 505, 0xFF1B4332),

        Paper("7", "Observation of Gravitational Waves from Neutron Star Mergers", listOf("K. Thorne", "R. Weiss"), "Physics", 92100, 4521, 3100, 0xFF16213E),
        Paper("8", "Room-Temperature Superconductivity Under High Pressure", listOf("E. Snider"), "Physics", 54200, 1890, 1330, 0xFF16213E),
        Paper("9", "Quantum Entanglement Across Metropolitan Fiber Networks", listOf("A. Zeilinger", "J. Wang"), "Physics", 38700, 1120, 940, 0xFF16213E),
        Paper("10", "Dark Matter Halo Structure from Galactic Rotation Curves", listOf("V. Rubin", "M. Ford"), "Physics", 44500, 1340, 1010, 0xFF16213E),
        Paper("11", "Topological Insulators for Fault-Tolerant Qubits", listOf("C. Kane"), "Physics", 26300, 870, 610, 0xFF16213E),
        Paper("12", "Hawking Radiation Analogs in Sonic Black Holes", listOf("J. Steinhauer"), "Physics", 19700, 520, 380, 0xFF16213E),

        Paper("13", "Transformer Architectures for Long-Context Reasoning", listOf("A. Vaswani", "N. Shazeer"), "CS", 88400, 3900, 2700, 0xFF2D3561),
        Paper("14", "Byzantine Fault Tolerance in Distributed Ledgers", listOf("L. Lamport"), "CS", 32100, 990, 715, 0xFF2D3561),
        Paper("15", "Differential Privacy Guarantees in Federated Learning", listOf("C. Dwork", "K. Talwar"), "CS", 27600, 845, 590, 0xFF2D3561),
        Paper("16", "Neural Architecture Search for Edge Devices", listOf("B. Zoph"), "CS", 22900, 690, 470, 0xFF2D3561),
        Paper("17", "Formal Verification of Concurrent Data Structures", listOf("M. Herlihy"), "CS", 17500, 430, 300, 0xFF2D3561),
        Paper("18", "Graph Neural Networks for Drug Discovery", listOf("J. Leskovec"), "CS", 41200, 1250, 980, 0xFF2D3561),

        Paper("19", "Catalytic Conversion of CO2 into Liquid Fuels", listOf("O. Yaghi"), "Chemistry", 29800, 810, 560, 0xFF3D0C11),
        Paper("20", "Self-Assembling Peptide Nanostructures", listOf("S. Zhang"), "Chemistry", 18300, 470, 320, 0xFF3D0C11),
        Paper("21", "Green Synthesis of Perovskite Solar Cells", listOf("M. Gratzel"), "Chemistry", 36500, 1080, 830, 0xFF3D0C11),
        Paper("22", "Enzymatic Degradation of Ocean Microplastics", listOf("A. Alcalde"), "Chemistry", 24700, 640, 450, 0xFF3D0C11),
        Paper("23", "Metal-Organic Frameworks for Hydrogen Storage", listOf("O. Yaghi", "H. Furukawa"), "Chemistry", 20100, 540, 380, 0xFF3D0C11),
        Paper("24", "Click Chemistry Approaches to Bioconjugation", listOf("K. Sharpless"), "Chemistry", 33400, 950, 700, 0xFF3D0C11),

        Paper("25", "Synaptic Plasticity Mechanisms in Long-Term Memory", listOf("E. Kandel"), "Neuroscience", 47600, 1560, 1120, 0xFF0F3460),
        Paper("26", "Brain-Computer Interfaces for Motor Restoration", listOf("K. Shenoy"), "Neuroscience", 61200, 2340, 1780, 0xFF0F3460),
        Paper("27", "The Role of Sleep in Memory Consolidation", listOf("M. Walker"), "Neuroscience", 39800, 1290, 940, 0xFF0F3460),
        Paper("28", "Neural Correlates of Decision-Making Under Uncertainty", listOf("P. Glimcher"), "Neuroscience", 25300, 710, 500, 0xFF0F3460),
        Paper("29", "Optogenetic Control of Fear Response Circuits", listOf("K. Deisseroth"), "Neuroscience", 43900, 1410, 1050, 0xFF0F3460),
        Paper("30", "Mapping the Connectome of the Fruit Fly Brain", listOf("D. Bock", "S. Plaza"), "Neuroscience", 30500, 890, 630, 0xFF0F3460)
    )
}
