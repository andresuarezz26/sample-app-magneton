package com.example.myapplication.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetFieldsUseCase
import com.example.myapplication.domain.usecase.SaveInterestsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val getFieldsUseCase: GetFieldsUseCase = GetFieldsUseCase(),
    private val saveInterestsUseCase: SaveInterestsUseCase = SaveInterestsUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    init {
        onIntent(OnboardingIntent.LoadFields)
    }

    fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            OnboardingIntent.LoadFields -> loadFields()
            is OnboardingIntent.ToggleField -> toggleField(intent.fieldId)
            is OnboardingIntent.UpdateEmail -> _state.update { it.copy(institutionalEmail = intent.email) }
        }
    }

    private fun loadFields() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val fields = getFieldsUseCase()
            _state.update { it.copy(fields = fields, isLoading = false) }
        }
    }

    private fun toggleField(fieldId: String) {
        _state.update { current ->
            val selected = current.selectedFieldIds.toMutableSet()
            if (!selected.add(fieldId)) selected.remove(fieldId)
            current.copy(selectedFieldIds = selected)
        }
    }

    /**
     * Persists the chosen interests (mock POST) and suspends until the save completes,
     * so the caller can durably persist the `onboarding_complete` flag and navigate only
     * after the write has finished. The DataStore flag itself is owned by the navigation layer.
     */
    suspend fun saveInterests() {
        val current = _state.value
        saveInterestsUseCase(
            fieldIds = current.selectedFieldIds,
            institutionalEmail = current.institutionalEmail.takeIf { it.isNotBlank() }
        )
    }
}
