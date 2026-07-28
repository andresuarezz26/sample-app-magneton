package com.example.myapplication.upload

import com.example.myapplication.domain.usecase.UploadPhotosUseCase
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
}
