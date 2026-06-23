package com.example.myapplication.data.model

data class Paper(
    val id: String,
    val title: String,
    val authors: String,
    val field: ScientificField,
    val summary: String
)

enum class ScientificField(val displayName: String) {
    BIOLOGY("Biology"),
    PHYSICS("Physics"),
    COMPUTER_SCIENCE("CS"),
    CHEMISTRY("Chemistry"),
    NEUROSCIENCE("Neuroscience"),
    EARTH_SCIENCE("Earth")
}
