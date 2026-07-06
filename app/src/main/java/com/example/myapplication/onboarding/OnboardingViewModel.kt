package com.example.myapplication.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.onboarding.OnboardingPreferencesRepository
import com.example.myapplication.domain.usecase.GetFieldsUseCase
import com.example.myapplication.domain.usecase.SubmitInterestsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val onboardingPreferences: OnboardingPreferencesRepository,
    private val getFieldsUseCase: GetFieldsUseCase = GetFieldsUseCase(),
    private val submitInterestsUseCase: SubmitInterestsUseCase = SubmitInterestsUseCase()
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
            is OnboardingIntent.EmailChanged -> _state.update { it.copy(institutionalEmail = intent.email) }
            OnboardingIntent.SubmitEmail -> complete()
            OnboardingIntent.SkipEmail -> complete()
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
            val selected = current.selectedFieldIds
            current.copy(
                selectedFieldIds = if (fieldId in selected) selected - fieldId else selected + fieldId
            )
        }
    }

    private fun complete() {
        viewModelScope.launch {
            submitInterestsUseCase(_state.value.selectedFieldIds.toList())
            onboardingPreferences.setComplete()
            _state.update { it.copy(isComplete = true) }
        }
    }

    companion object {
        fun factory(onboardingPreferences: OnboardingPreferencesRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OnboardingViewModel(onboardingPreferences) as T
                }
            }
    }
}
