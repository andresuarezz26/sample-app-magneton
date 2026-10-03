package com.example.myapplication.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UploadPhotosUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase = UploadPhotosUseCase(testDispatcher)

    @Test
    fun `returns success for a non-empty selection`() = runTest(testDispatcher) {
        val result = useCase(listOf("u1", "u2"))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `simulates a 1200ms upload`() = runTest(testDispatcher) {
        useCase(listOf("u1"))

        assertEquals(1200L, currentTime)
    }
}
