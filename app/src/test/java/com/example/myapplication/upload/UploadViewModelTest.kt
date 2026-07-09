package com.example.myapplication.upload

import com.example.myapplication.data.model.PaperStatus
import com.example.myapplication.data.model.UploadSource
import com.example.myapplication.domain.usecase.PollPaperStatusUseCase
import com.example.myapplication.domain.usecase.UploadPaperUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

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

    private fun viewModel(
        uploadUseCase: UploadPaperUseCase = UploadPaperUseCase(testDispatcher)
    ) = UploadViewModel(
        uploadPaperUseCase = uploadUseCase,
        pollPaperStatusUseCase = PollPaperStatusUseCase(pollIntervalMs = 5_000L, processingPolls = 2)
    )

    @Test
    fun `doi submit uploads then polls to completed`() = runTest(testDispatcher) {
        val viewModel = viewModel()
        viewModel.onIntent(UploadIntent.SelectSource(UploadSource.DOI))
        viewModel.onIntent(UploadIntent.EnterDoi("10.1038/s41586-021-03819-2"))

        viewModel.onIntent(UploadIntent.Submit)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(UploadPhase.COMPLETED, state.phase)
        assertEquals(PaperStatus.COMPLETED, state.status)
        assertFalse(state.isUploading)
        assertNotNull(state.paperId)
        assertNull(state.error)
        assertEquals(1f, state.progress, 0.0001f)
    }

    @Test
    fun `submitting doi transitions through processing before completing`() = runTest(testDispatcher) {
        // Long-running poll so completion is far in the future; observe the PROCESSING phase
        // partway through by advancing virtual time past the upload but before completion.
        val viewModel = UploadViewModel(
            uploadPaperUseCase = UploadPaperUseCase(testDispatcher),
            pollPaperStatusUseCase = PollPaperStatusUseCase(pollIntervalMs = 5_000L, processingPolls = 100)
        )
        viewModel.onIntent(UploadIntent.SelectSource(UploadSource.DOI))
        viewModel.onIntent(UploadIntent.EnterDoi("10.1038/s41586-021-03819-2"))

        viewModel.onIntent(UploadIntent.Submit)
        advanceTimeBy(6_000)
        runCurrent()

        val state = viewModel.state.value
        assertEquals(UploadPhase.PROCESSING, state.phase)
        assertEquals(PaperStatus.PROCESSING, state.status)
        assertNotNull(state.paperId)
    }

    @Test
    fun `upload failure surfaces error and retry recovers`() = runTest(testDispatcher) {
        var shouldFail = true
        val flaky = object : UploadPaperUseCase(testDispatcher) {
            override suspend fun invoke(source: UploadSource, reference: String): String {
                if (shouldFail) throw IOException("Network unavailable")
                return super.invoke(source, reference)
            }
        }
        val viewModel = viewModel(uploadUseCase = flaky)
        viewModel.onIntent(UploadIntent.SelectSource(UploadSource.DOI))
        viewModel.onIntent(UploadIntent.EnterDoi("10.1038/s41586-021-03819-2"))

        viewModel.onIntent(UploadIntent.Submit)
        advanceUntilIdle()

        val failed = viewModel.state.value
        assertEquals(UploadPhase.FORM, failed.phase)
        assertEquals(PaperStatus.FAILED, failed.status)
        assertNotNull(failed.error)
        assertFalse(failed.isUploading)

        // Retry once the backend recovers.
        shouldFail = false
        viewModel.onIntent(UploadIntent.Retry)
        advanceUntilIdle()

        val recovered = viewModel.state.value
        assertEquals(UploadPhase.COMPLETED, recovered.phase)
        assertNull(recovered.error)
    }

    @Test
    fun `submitting blank doi shows validation error without uploading`() = runTest(testDispatcher) {
        val viewModel = viewModel()
        viewModel.onIntent(UploadIntent.SelectSource(UploadSource.DOI))

        viewModel.onIntent(UploadIntent.Submit)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.error != null)
        assertEquals(UploadPhase.FORM, state.phase)
        assertNull(state.paperId)
    }
}
