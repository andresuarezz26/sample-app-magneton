package com.example.myapplication.generate

import com.example.myapplication.data.llm.GeneratedImageStore
import com.example.myapplication.domain.llm.LocalImageGenerator
import com.example.myapplication.domain.usecase.UploadPhotosUseCase
import kotlinx.coroutines.CompletableDeferred
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeLocalImageGenerator : LocalImageGenerator {
    var calls = 0
    var gate: CompletableDeferred<Unit>? = null
    var result: Result<ByteArray> = Result.success(byteArrayOf(1, 2, 3))

    override suspend fun generate(prompt: String): Result<ByteArray> {
        calls++
        gate?.await()
        return result
    }
}

private class FakeGeneratedImageStore : GeneratedImageStore {
    override suspend fun save(png: ByteArray): String = "content://fake/generated.png"
}

@OptIn(ExperimentalCoroutinesApi::class)
class GenerateViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val generator = FakeLocalImageGenerator()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() =
        GenerateViewModel(generator, FakeGeneratedImageStore(), UploadPhotosUseCase(testDispatcher))

    @Test
    fun `prompt change updates prompt`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.onIntent(GenerateIntent.PromptChanged("a cat"))
        assertEquals("a cat", vm.state.value.prompt)
    }

    @Test
    fun `generate with blank prompt does nothing`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.onIntent(GenerateIntent.PromptChanged("  "))
        vm.onIntent(GenerateIntent.Generate)
        advanceUntilIdle()
        assertEquals(0, generator.calls)
    }

    @Test
    fun `generate success sets generatedImageUri`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.onIntent(GenerateIntent.PromptChanged("a cat"))
        vm.onIntent(GenerateIntent.Generate)
        advanceUntilIdle()
        assertEquals("content://fake/generated.png", vm.state.value.generatedImageUri)
        assertFalse(vm.state.value.isGenerating)
    }

    @Test
    fun `generate failure sets errorMessage`() = runTest(testDispatcher) {
        generator.result = Result.failure(IllegalStateException("Model not installed"))
        val vm = viewModel()
        vm.onIntent(GenerateIntent.PromptChanged("a cat"))
        vm.onIntent(GenerateIntent.Generate)
        advanceUntilIdle()
        assertEquals("Model not installed", vm.state.value.errorMessage)
        assertNull(vm.state.value.generatedImageUri)
        assertFalse(vm.state.value.isGenerating)
    }

    @Test
    fun `isGenerating is true while generator is suspended`() = runTest(testDispatcher) {
        generator.gate = CompletableDeferred()
        val vm = viewModel()
        vm.onIntent(GenerateIntent.PromptChanged("a cat"))
        vm.onIntent(GenerateIntent.Generate)
        advanceUntilIdle()
        assertTrue(vm.state.value.isGenerating)
        generator.gate!!.complete(Unit)
        advanceUntilIdle()
        assertFalse(vm.state.value.isGenerating)
    }

    @Test
    fun `upload with generated image sets uploadSuccess`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.onIntent(GenerateIntent.PromptChanged("a cat"))
        vm.onIntent(GenerateIntent.Generate)
        advanceUntilIdle()
        vm.onIntent(GenerateIntent.UploadImage)
        advanceUntilIdle()
        assertTrue(vm.state.value.uploadSuccess)
        assertFalse(vm.state.value.isUploading)
    }

    @Test
    fun `start over resets state`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.onIntent(GenerateIntent.PromptChanged("a cat"))
        vm.onIntent(GenerateIntent.StartOver)
        assertEquals(GenerateUiState(), vm.state.value)
    }
}
