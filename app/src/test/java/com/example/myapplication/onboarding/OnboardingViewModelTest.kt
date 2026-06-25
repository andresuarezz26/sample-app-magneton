package com.example.myapplication.onboarding

import com.example.myapplication.domain.usecase.GetFieldsUseCase
import com.example.myapplication.domain.usecase.SaveInterestsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
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

    private fun newViewModel() = OnboardingViewModel(
        getFieldsUseCase = GetFieldsUseCase(testDispatcher),
        saveInterestsUseCase = SaveInterestsUseCase(testDispatcher)
    )

    @Test
    fun `initial load populates fields and isLoading is false`() = runTest(testDispatcher) {
        val viewModel = newViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.fields.isNotEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `canContinue is false with no selection and true after selecting a field`() = runTest(testDispatcher) {
        val viewModel = newViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.canContinue)

        val fieldId = viewModel.state.value.fields.first().id
        viewModel.onIntent(OnboardingIntent.ToggleField(fieldId))

        assertTrue(viewModel.state.value.canContinue)
    }

    @Test
    fun `toggling a selected field deselects it`() = runTest(testDispatcher) {
        val viewModel = newViewModel()
        advanceUntilIdle()

        val fieldId = viewModel.state.value.fields.first().id
        viewModel.onIntent(OnboardingIntent.ToggleField(fieldId))
        viewModel.onIntent(OnboardingIntent.ToggleField(fieldId))

        assertFalse(viewModel.state.value.selectedFieldIds.contains(fieldId))
        assertFalse(viewModel.state.value.canContinue)
    }

    @Test
    fun `saveInterests completes and preserves selected fields`() = runTest(testDispatcher) {
        val viewModel = newViewModel()
        advanceUntilIdle()

        val fieldId = viewModel.state.value.fields.first().id
        viewModel.onIntent(OnboardingIntent.ToggleField(fieldId))

        viewModel.saveInterests()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.selectedFieldIds.contains(fieldId))
    }
}
