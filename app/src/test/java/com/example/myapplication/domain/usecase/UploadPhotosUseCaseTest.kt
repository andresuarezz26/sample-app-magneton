package com.example.myapplication.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UploadPhotosUseCaseTest {

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
    fun `uploading a non-empty list of uris succeeds`() = runTest(testDispatcher) {
        val uploadPhotosUseCase = UploadPhotosUseCase(testDispatcher)

        val result = uploadPhotosUseCase(listOf("uri1", "uri2"))

        assertTrue(result.isSuccess)
    }
}
