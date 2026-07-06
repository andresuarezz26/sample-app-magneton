package com.example.myapplication.onboarding

import com.example.myapplication.domain.usecase.GetFieldsUseCase
import com.example.myapplication.domain.usecase.SubmitInterestsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        preferences: FakeOnboardingPreferencesRepository = FakeOnboardingPreferencesRepository()
    ) = OnboardingViewModel(
        onboardingPreferences = preferences,
        getFieldsUseCase = GetFieldsUseCase(testDispatcher),
        submitInterestsUseCase = SubmitInterestsUseCase(testDispatcher)
    )

    @Test
    fun `initial load populates fields and isLoading is false`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(8, state.fields.size)
        assertFalse(state.isLoading)
    }

    @Test
    fun `toggling a field selects it and toggling again deselects it`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(OnboardingIntent.ToggleField("physics"))
        assertTrue(viewModel.state.value.selectedFieldIds.contains("physics"))

        viewModel.onIntent(OnboardingIntent.ToggleField("physics"))
        assertFalse(viewModel.state.value.selectedFieldIds.contains("physics"))
    }

    @Test
    fun `canContinue is false with zero selections and true with at least one`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.canContinue)

        viewModel.onIntent(OnboardingIntent.ToggleField("biology"))
        assertTrue(viewModel.state.value.canContinue)
    }

    @Test
    fun `skipping email completes onboarding and persists the flag`() = runTest(testDispatcher) {
        val preferences = FakeOnboardingPreferencesRepository()
        val viewModel = createViewModel(preferences)
        advanceUntilIdle()

        viewModel.onIntent(OnboardingIntent.ToggleField("physics"))
        viewModel.onIntent(OnboardingIntent.SkipEmail)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isComplete)
        assertEquals(1, preferences.setCompleteCallCount)
    }

    @Test
    fun `submitting email completes onboarding and persists the flag`() = runTest(testDispatcher) {
        val preferences = FakeOnboardingPreferencesRepository()
        val viewModel = createViewModel(preferences)
        advanceUntilIdle()

        viewModel.onIntent(OnboardingIntent.ToggleField("physics"))
        viewModel.onIntent(OnboardingIntent.EmailChanged("student@university.edu"))
        viewModel.onIntent(OnboardingIntent.SubmitEmail)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isComplete)
        assertEquals(1, preferences.setCompleteCallCount)
    }
}
