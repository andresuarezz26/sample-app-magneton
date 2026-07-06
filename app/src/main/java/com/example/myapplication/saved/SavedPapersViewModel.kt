package com.example.myapplication.saved

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SavedPapersViewModel : ViewModel() {

    private val _state = MutableStateFlow(SavedPapersUiState())
    val state: StateFlow<SavedPapersUiState> = _state.asStateFlow()
}
