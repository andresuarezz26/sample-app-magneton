package com.example.myapplication.tutorial

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TutorialViewModel : ViewModel() {

    private val _state = MutableStateFlow(TutorialUiState())
    val state: StateFlow<TutorialUiState> = _state.asStateFlow()

    fun onIntent(intent: TutorialIntent) {
        when (intent) {
            TutorialIntent.NextPage -> nextPage()
            TutorialIntent.PreviousPage -> previousPage()
            TutorialIntent.SkipTutorial -> skip()
            TutorialIntent.CompleteTutorial -> complete()
        }
    }

    private fun nextPage() {
        _state.update { current ->
            if (current.currentPage < current.totalPages - 1) {
                current.copy(currentPage = current.currentPage + 1)
            } else {
                current
            }
        }
    }

    private fun previousPage() {
        _state.update { current ->
            if (current.currentPage > 0) {
                current.copy(currentPage = current.currentPage - 1)
            } else {
                current
            }
        }
    }

    private fun skip() {
        // Skip intent completion is handled by the UI layer calling onComplete()
    }

    private fun complete() {
        // Completion is handled by the caller (MainActivity) via state observation
    }
}
