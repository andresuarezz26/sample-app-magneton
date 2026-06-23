package com.example.myapplication.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetMyPapersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getMyPapersUseCase: GetMyPapersUseCase = GetMyPapersUseCase()
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun loadPapers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val papers = getMyPapersUseCase()
            _state.value = _state.value.copy(papers = papers, isLoading = false)
        }
    }
}

class ProfileViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(GetMyPapersUseCase()) as T
    }
}
