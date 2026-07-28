package com.example.myapplication.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetCommentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val getCommentsUseCase: GetCommentsUseCase = GetCommentsUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(CommentsUiState())
    val state: StateFlow<CommentsUiState> = _state.asStateFlow()

    fun onIntent(intent: CommentsIntent) {
        when (intent) {
            is CommentsIntent.LoadComments -> loadComments(intent.videoId)
            is CommentsIntent.LikeComment -> likeComment(intent.commentId)
        }
    }

    private fun loadComments(videoId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val comments = getCommentsUseCase(videoId)
            _state.update { it.copy(isLoading = false, comments = comments) }
        }
    }

    private fun likeComment(commentId: String) {
        _state.update { current ->
            current.copy(
                comments = current.comments.map { comment ->
                    if (comment.id == commentId) comment.copy(likes = comment.likes + 1) else comment
                }
            )
        }
    }
}
