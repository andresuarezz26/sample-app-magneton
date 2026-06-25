package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.ScientificField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Mock use case simulating GET /api/v1/fields with an offline cache.
 * Returns a hardcoded list of scientific fields.
 */
class GetFieldsUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(): List<ScientificField> = withContext(ioDispatcher) {
        listOf(
            ScientificField(id = "cs", name = "Computer Science"),
            ScientificField(id = "biology", name = "Biology"),
            ScientificField(id = "physics", name = "Physics"),
            ScientificField(id = "chemistry", name = "Chemistry"),
            ScientificField(id = "neuroscience", name = "Neuroscience"),
            ScientificField(id = "mathematics", name = "Mathematics"),
            ScientificField(id = "astronomy", name = "Astronomy"),
            ScientificField(id = "earth_science", name = "Earth Science"),
            ScientificField(id = "medicine", name = "Medicine"),
            ScientificField(id = "engineering", name = "Engineering")
        )
    }
}
