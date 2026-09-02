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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

private class FakeUploadPhotosUseCase(
    var result: Result<Unit> = Result.success(Unit)
) : UploadPhotosUseCase() {
    val receivedCalls = mutableListOf<List<String>>()
    override suspend fun invoke(photoUris: List<String>): Result<Unit> {
        receivedCalls += photoUris
        return result
    }
}

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

    @Test
    fun `selecting the same photo twice keeps a single entry`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(UploadPhotosUseCase(testDispatcher))
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1", "uri2")))

        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri2", "uri3")))

        assertEquals(listOf("uri1", "uri2", "uri3"), viewModel.state.value.selectedPhotos)
    }

    @Test
    fun `removing an unknown photo leaves selectedPhotos unchanged`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(UploadPhotosUseCase(testDispatcher))
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1")))

        viewModel.onIntent(UploadIntent.RemovePhoto("nope"))

        assertEquals(listOf("uri1"), viewModel.state.value.selectedPhotos)
    }

    @Test
    fun `upload failure sets errorMessage and clears isUploading`() = runTest(testDispatcher) {
        val fake = FakeUploadPhotosUseCase(Result.failure(IllegalStateException("Network down")))
        val viewModel = UploadViewModel(fake)
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1")))

        viewModel.onIntent(UploadIntent.UploadPhotos)
        advanceUntilIdle()

        assertEquals("Network down", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isUploading)
        assertFalse(viewModel.state.value.uploadSuccess)
        assertEquals(listOf("uri1"), viewModel.state.value.selectedPhotos)
    }

    @Test
    fun `dismissing error clears errorMessage but keeps selection`() = runTest(testDispatcher) {
        val fake = FakeUploadPhotosUseCase(Result.failure(IllegalStateException("Network down")))
        val viewModel = UploadViewModel(fake)
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1")))
        viewModel.onIntent(UploadIntent.UploadPhotos)
        advanceUntilIdle()

        viewModel.onIntent(UploadIntent.DismissError)

        assertNull(viewModel.state.value.errorMessage)
        assertEquals(listOf("uri1"), viewModel.state.value.selectedPhotos)
        assertFalse(viewModel.state.value.uploadSuccess)
    }

    @Test
    fun `retrying after a failure clears the previous error and reports success`() = runTest(testDispatcher) {
        val fake = FakeUploadPhotosUseCase(Result.failure(IllegalStateException("Network down")))
        val viewModel = UploadViewModel(fake)
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1")))

        viewModel.onIntent(UploadIntent.UploadPhotos)
        advanceUntilIdle()

        fake.result = Result.success(Unit)
        viewModel.onIntent(UploadIntent.UploadPhotos)
        advanceUntilIdle()

        assertNull(viewModel.state.value.errorMessage)
        assertTrue(viewModel.state.value.uploadSuccess)
    }

    @Test
    fun `starting over after a failure resets to default state`() = runTest(testDispatcher) {
        val fake = FakeUploadPhotosUseCase(Result.failure(IllegalStateException("Network down")))
        val viewModel = UploadViewModel(fake)
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1")))
        viewModel.onIntent(UploadIntent.UploadPhotos)
        advanceUntilIdle()

        viewModel.onIntent(UploadIntent.StartOver)

        assertEquals(UploadUiState(), viewModel.state.value)
    }

    @Test
    fun `upload passes exactly the selected photos to the use case`() = runTest(testDispatcher) {
        val fake = FakeUploadPhotosUseCase()
        val viewModel = UploadViewModel(fake)
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("uri1", "uri2")))
        viewModel.onIntent(UploadIntent.RemovePhoto("uri1"))

        viewModel.onIntent(UploadIntent.UploadPhotos)
        advanceUntilIdle()

        assertEquals(listOf(listOf("uri2")), fake.receivedCalls)
    }

    @Test
    fun `empty selection never invokes the use case`() = runTest(testDispatcher) {
        val fake = FakeUploadPhotosUseCase()
        val viewModel = UploadViewModel(fake)

        viewModel.onIntent(UploadIntent.UploadPhotos)
        advanceUntilIdle()

        assertTrue(fake.receivedCalls.isEmpty())
    }

    @Test
    fun `dismissing error when there is none is a no-op`() = runTest(testDispatcher) {
        val viewModel = UploadViewModel(UploadPhotosUseCase(testDispatcher))

        viewModel.onIntent(UploadIntent.DismissError)

        assertEquals(UploadUiState(), viewModel.state.value)
    }
}
