package com.example.myapplication.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetCommentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommentViewModel(
    private val getCommentsUseCase: GetCommentsUseCase = GetCommentsUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(CommentUiState())
    val state: StateFlow<CommentUiState> = _state.asStateFlow()

    init {
        onIntent(CommentIntent.LoadComments)
    }

    fun onIntent(intent: CommentIntent) {
        when (intent) {
            CommentIntent.LoadComments -> loadComments()
        }
    }

    private fun loadComments() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val comments = getCommentsUseCase()
            _state.update { it.copy(isLoading = false, comments = comments) }
        }
    }
}
