package com.example.myapplication.upload

import android.net.Uri
import com.example.myapplication.domain.usecase.UploadPaperUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UploadViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submitDoi with valid doi emits paperId`() = runTest(testDispatcher) {
        val uploadUseCase = UploadPaperUseCase(testDispatcher)
        val viewModel = UploadViewModel(uploadUseCase)

        viewModel.onIntent(UploadIntent.SelectTab(1))
        viewModel.onIntent(UploadIntent.EnterDoi("10.1038/s41586-021-03819-2"))
        viewModel.onIntent(UploadIntent.SubmitDoi)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.paperId)
        assertEquals(null, state.error)
    }

    @Test
    fun `submitFile without selecting pdf shows error`() = runTest(testDispatcher) {
        val uploadUseCase = UploadPaperUseCase(testDispatcher)
        val viewModel = UploadViewModel(uploadUseCase)

        viewModel.onIntent(UploadIntent.SubmitFile)

        val state = viewModel.state.value
        assertNotNull(state.error)
        assertEquals(null, state.paperId)
    }

    @Test
    fun `submitDoi without entering doi shows error`() = runTest(testDispatcher) {
        val uploadUseCase = UploadPaperUseCase(testDispatcher)
        val viewModel = UploadViewModel(uploadUseCase)

        viewModel.onIntent(UploadIntent.SelectTab(1))
        viewModel.onIntent(UploadIntent.SubmitDoi)

        val state = viewModel.state.value
        assertNotNull(state.error)
        assertEquals(null, state.paperId)
    }

    @Test
    fun `selectTab changes selectedTab`() = runTest(testDispatcher) {
        val uploadUseCase = UploadPaperUseCase(testDispatcher)
        val viewModel = UploadViewModel(uploadUseCase)

        assertEquals(0, viewModel.state.value.selectedTab)

        viewModel.onIntent(UploadIntent.SelectTab(1))

        assertEquals(1, viewModel.state.value.selectedTab)
    }

    @Test
    fun `retry after failure resubmits doi`() = runTest(testDispatcher) {
        val uploadUseCase = UploadPaperUseCase(testDispatcher)
        val viewModel = UploadViewModel(uploadUseCase)

        viewModel.onIntent(UploadIntent.SelectTab(1))
        viewModel.onIntent(UploadIntent.SubmitDoi)
        var state = viewModel.state.value
        assertNotNull(state.error)

        viewModel.onIntent(UploadIntent.EnterDoi("10.1038/s41586-021-03819-2"))
        viewModel.onIntent(UploadIntent.Retry)
        advanceUntilIdle()

        state = viewModel.state.value
        assertNotNull(state.paperId)
        assertEquals(null, state.error)
    }
}
