package com.example.myapplication.upload

import com.example.myapplication.data.model.PaperStatus
import com.example.myapplication.domain.usecase.PollPaperStatusUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PollPaperStatusUseCaseTest {

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
    fun `status flow emits QUEUED then PROCESSING then COMPLETED`() = runTest(testDispatcher) {
        val pollUseCase = PollPaperStatusUseCase(testDispatcher)
        val statuses = pollUseCase("paper_123").toList()

        assertEquals(7, statuses.size)
        assertEquals(PaperStatus.QUEUED, statuses[0])
        assertEquals(PaperStatus.PROCESSING, statuses[1])
        assertEquals(PaperStatus.PROCESSING, statuses[2])
        assertEquals(PaperStatus.PROCESSING, statuses[3])
        assertEquals(PaperStatus.PROCESSING, statuses[4])
        assertEquals(PaperStatus.PROCESSING, statuses[5])
        assertEquals(PaperStatus.COMPLETED, statuses[6])
    }

    @Test
    fun `first emission is always QUEUED`() = runTest(testDispatcher) {
        val pollUseCase = PollPaperStatusUseCase(testDispatcher)
        val firstStatus = pollUseCase("paper_123").toList().first()

        assertEquals(PaperStatus.QUEUED, firstStatus)
    }

    @Test
    fun `last emission is always COMPLETED`() = runTest(testDispatcher) {
        val pollUseCase = PollPaperStatusUseCase(testDispatcher)
        val lastStatus = pollUseCase("paper_456").toList().last()

        assertEquals(PaperStatus.COMPLETED, lastStatus)
    }
}
