package com.example.myapplication.onboarding

import com.example.myapplication.domain.model.ScientificField

data class OnboardingUiState(
    val fields: List<ScientificField> = emptyList(),
    val selectedFieldIds: Set<String> = emptySet(),
    val institutionalEmail: String = "",
    val isLoading: Boolean = false,
    val isComplete: Boolean = false
) {
    val canContinue: Boolean get() = selectedFieldIds.isNotEmpty()
}
