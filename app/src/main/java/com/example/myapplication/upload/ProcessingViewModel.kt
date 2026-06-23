package com.example.myapplication.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.PollPaperStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProcessingViewModel(
    paperId: String,
    private val pollStatusUseCase: PollPaperStatusUseCase = PollPaperStatusUseCase()
) : ViewModel() {
    private val _state = MutableStateFlow(ProcessingUiState())
    val state: StateFlow<ProcessingUiState> = _state.asStateFlow()

    init {
        pollStatusUseCase(paperId).onEach { status ->
            _state.value = ProcessingUiState(status = status)
        }.launchIn(viewModelScope)
    }
}

class ProcessingViewModelFactory(private val paperId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProcessingViewModel(paperId, PollPaperStatusUseCase()) as T
    }
}
