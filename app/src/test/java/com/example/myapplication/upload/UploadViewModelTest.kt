package com.example.myapplication.upload

import com.example.myapplication.domain.usecase.UploadPhotosUseCase
import com.example.myapplication.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UploadViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val uploadPhotosUseCase = mockk<UploadPhotosUseCase>()
    private lateinit var viewModel: UploadViewModel

    @Before
    fun setUp() {
        coEvery { uploadPhotosUseCase(any()) } returns Result.success(Unit)
        viewModel = UploadViewModel(uploadPhotosUseCase)
    }

    private fun failUpload(message: String = "Network down") {
        coEvery { uploadPhotosUseCase(any()) } returns Result.failure(IOException(message))
    }

    @Test
    fun `initial state is the default UploadUiState`() {
        assertEquals(UploadUiState(), viewModel.state.value)
    }

    @Test
    fun `PhotosSelected populates selectedPhotos`() {
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u1", "u2")))

        assertEquals(listOf("u1", "u2"), viewModel.state.value.selectedPhotos)
    }

    @Test
    fun `PhotosSelected appends to the existing selection and drops duplicates`() {
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u1", "u2")))
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u2", "u3")))

        assertEquals(listOf("u1", "u2", "u3"), viewModel.state.value.selectedPhotos)
    }

    @Test
    fun `RemovePhoto removes only that uri`() {
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u1", "u2", "u3")))

        viewModel.onIntent(UploadIntent.RemovePhoto("u2"))

        assertEquals(listOf("u1", "u3"), viewModel.state.value.selectedPhotos)
    }

    @Test
    fun `RemovePhoto with an unknown uri leaves the selection unchanged`() {
        viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u1", "u2")))

        viewModel.onIntent(UploadIntent.RemovePhoto("missing"))

        assertEquals(listOf("u1", "u2"), viewModel.state.value.selectedPhotos)
    }

    @Test
    fun `UploadPhotos with an empty selection does nothing and never calls the use case`() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel.onIntent(UploadIntent.UploadPhotos)
            advanceUntilIdle()

            coVerify(exactly = 0) { uploadPhotosUseCase(any()) }
            assertEquals(UploadUiState(), viewModel.state.value)
        }

    @Test
    fun `UploadPhotos sets isUploading while in flight then uploadSuccess on success`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val gate = CompletableDeferred<Result<Unit>>()
            coEvery { uploadPhotosUseCase(any()) } coAnswers { gate.await() }
            viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u1")))

            viewModel.onIntent(UploadIntent.UploadPhotos)
            runCurrent()

            assertTrue(viewModel.state.value.isUploading)
            assertFalse(viewModel.state.value.uploadSuccess)

            gate.complete(Result.success(Unit))
            advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse(state.isUploading)
            assertTrue(state.uploadSuccess)
            assertNull(state.errorMessage)
        }

    @Test
    fun `UploadPhotos passes the current selection to the use case`() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u1", "u2")))

            viewModel.onIntent(UploadIntent.UploadPhotos)
            advanceUntilIdle()

            coVerify(exactly = 1) { uploadPhotosUseCase(listOf("u1", "u2")) }
        }

    @Test
    fun `UploadPhotos failure surfaces the error message and clears isUploading`() =
        runTest(mainDispatcherRule.testDispatcher) {
            failUpload("Network down")
            viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u1", "u2")))

            viewModel.onIntent(UploadIntent.UploadPhotos)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals("Network down", state.errorMessage)
            assertFalse(state.isUploading)
            assertFalse(state.uploadSuccess)
            assertEquals(listOf("u1", "u2"), state.selectedPhotos)
        }

    @Test
    fun `UploadPhotos clears a previous error before retrying`() =
        runTest(mainDispatcherRule.testDispatcher) {
            failUpload()
            viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u1")))
            viewModel.onIntent(UploadIntent.UploadPhotos)
            advanceUntilIdle()
            assertEquals("Network down", viewModel.state.value.errorMessage)

            val gate = CompletableDeferred<Result<Unit>>()
            coEvery { uploadPhotosUseCase(any()) } coAnswers { gate.await() }
            viewModel.onIntent(UploadIntent.UploadPhotos)
            runCurrent()

            assertNull(viewModel.state.value.errorMessage)
            assertTrue(viewModel.state.value.isUploading)
        }

    @Test
    fun `DismissError clears errorMessage and keeps the selection`() =
        runTest(mainDispatcherRule.testDispatcher) {
            failUpload()
            viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u1")))
            viewModel.onIntent(UploadIntent.UploadPhotos)
            advanceUntilIdle()

            viewModel.onIntent(UploadIntent.DismissError)

            assertNull(viewModel.state.value.errorMessage)
            assertEquals(listOf("u1"), viewModel.state.value.selectedPhotos)
        }

    @Test
    fun `StartOver resets state to default after a successful upload`() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u1")))
            viewModel.onIntent(UploadIntent.UploadPhotos)
            advanceUntilIdle()
            assertTrue(viewModel.state.value.uploadSuccess)

            viewModel.onIntent(UploadIntent.StartOver)

            assertEquals(UploadUiState(), viewModel.state.value)
        }

    @Test
    fun `StartOver resets state from an error state`() =
        runTest(mainDispatcherRule.testDispatcher) {
            failUpload()
            viewModel.onIntent(UploadIntent.PhotosSelected(listOf("u1")))
            viewModel.onIntent(UploadIntent.UploadPhotos)
            advanceUntilIdle()
            assertEquals("Network down", viewModel.state.value.errorMessage)

            viewModel.onIntent(UploadIntent.StartOver)

            assertEquals(UploadUiState(), viewModel.state.value)
        }
}
