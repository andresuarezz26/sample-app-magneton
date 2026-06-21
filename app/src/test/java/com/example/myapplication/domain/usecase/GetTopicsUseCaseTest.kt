package com.example.myapplication.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetTopicsUseCaseTest {

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
    fun `returns all unique topics from feed videos sorted alphabetically`() = runTest {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val getTopicsUseCase = GetTopicsUseCase(getFeedUseCase, testDispatcher)

        val topics = getTopicsUseCase()

        assertEquals(7, topics.size)
        assertTrue(topics.contains("biology"))
        assertTrue(topics.contains("chemistry"))
        assertTrue(topics.contains("earth"))
        assertTrue(topics.contains("neuroscience"))
        assertTrue(topics.contains("physics"))
        assertTrue(topics.contains("quantum"))
        assertTrue(topics.contains("space"))
        assertEquals(topics, topics.sorted())
    }
}
