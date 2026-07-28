package com.example.myapplication.upload

import com.example.myapplication.domain.usecase.UploadPhotosUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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
    fun `selecting photos populates selectedPhotos`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(UploadPhotosUseCase(testDispatcher))

        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1", "uri2")))

        assertEquals(listOf("uri1", "uri2"), viewModel.state.value.selectedPhotos)
    }

    @Test
    fun `removing a photo filters it out of selectedPhotos`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(UploadPhotosUseCase(testDispatcher))
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1", "uri2")))

        viewModel.onIntent(UploadIntent.RemovePhoto("uri1"))

        assertEquals(listOf("uri2"), viewModel.state.value.selectedPhotos)
    }

    @Test
    fun `selecting an already-selected uri does not duplicate it`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(UploadPhotosUseCase(testDispatcher))
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1", "uri2")))

        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri2", "uri3")))

        assertEquals(listOf("uri1", "uri2", "uri3"), viewModel.state.value.selectedPhotos)
    }

    @Test
    fun `removing a uri that is not present is a no-op`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(UploadPhotosUseCase(testDispatcher))
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1", "uri2")))

        viewModel.onIntent(UploadIntent.RemovePhoto("uri-not-present"))

        assertEquals(listOf("uri1", "uri2"), viewModel.state.value.selectedPhotos)
    }

    @Test
    fun `dismissing the error clears errorMessage without touching other state`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(FakeFailingUploadPhotosUseCase(testDispatcher))
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1")))
        viewModel.onIntent(UploadIntent.UploadPhotos)
        advanceUntilIdle()
        val stateBeforeDismiss = viewModel.state.value
        assertEquals("upload failed", stateBeforeDismiss.errorMessage)

        viewModel.onIntent(UploadIntent.DismissError)

        val stateAfterDismiss = viewModel.state.value
        assertEquals(null, stateAfterDismiss.errorMessage)
        assertEquals(stateBeforeDismiss.selectedPhotos, stateAfterDismiss.selectedPhotos)
        assertEquals(stateBeforeDismiss.isUploading, stateAfterDismiss.isUploading)
        assertEquals(stateBeforeDismiss.uploadSuccess, stateAfterDismiss.uploadSuccess)
    }

    @Test
    fun `uploading with a failing use case sets errorMessage and leaves uploadSuccess false`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(FakeFailingUploadPhotosUseCase(testDispatcher))
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1")))

        viewModel.onIntent(UploadIntent.UploadPhotos)
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.state.value.isUploading)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isUploading)
        assertEquals("upload failed", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.uploadSuccess)
    }

    @Test
    fun `uploading with a non-empty selection sets isUploading then uploadSuccess`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(UploadPhotosUseCase(testDispatcher))
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1")))

        viewModel.onIntent(UploadIntent.UploadPhotos)
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.state.value.isUploading)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isUploading)
        assertTrue(viewModel.state.value.uploadSuccess)
    }

    @Test
    fun `uploading with an empty selection is a no-op`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(UploadPhotosUseCase(testDispatcher))

        viewModel.onIntent(UploadIntent.UploadPhotos)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isUploading)
        assertFalse(viewModel.state.value.uploadSuccess)
    }

    @Test
    fun `starting over resets state after a successful upload`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(UploadPhotosUseCase(testDispatcher))
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1")))
        viewModel.onIntent(UploadIntent.UploadPhotos)
        advanceUntilIdle()

        viewModel.onIntent(UploadIntent.StartOver)

        assertEquals(UploadUiState(), viewModel.state.value)
    }

    private class FakeFailingUploadPhotosUseCase(
        private val dispatcher: CoroutineDispatcher
    ) : UploadPhotosUseCase(dispatcher) {

        override suspend fun invoke(photoUris: List<String>): Result<Unit> =
            withContext(dispatcher) {
                delay(1200)
                Result.failure(RuntimeException("upload failed"))
            }
    }
}
