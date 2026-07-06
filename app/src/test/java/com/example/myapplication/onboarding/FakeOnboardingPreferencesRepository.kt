package com.example.myapplication.onboarding

import com.example.myapplication.data.onboarding.OnboardingPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeOnboardingPreferencesRepository : OnboardingPreferencesRepository {

    private val _isOnboardingComplete = MutableStateFlow(false)
    override val isOnboardingComplete: Flow<Boolean> = _isOnboardingComplete

    var setCompleteCallCount = 0
        private set

    override suspend fun setComplete() {
        setCompleteCallCount++
        _isOnboardingComplete.value = true
    }
}
