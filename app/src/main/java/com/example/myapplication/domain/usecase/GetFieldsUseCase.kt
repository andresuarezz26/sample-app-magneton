package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.ScientificField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetFieldsUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(): List<ScientificField> = withContext(ioDispatcher) {
        listOf(
            ScientificField(id = "physics", name = "Physics"),
            ScientificField(id = "biology", name = "Biology"),
            ScientificField(id = "computer_science", name = "Computer Science"),
            ScientificField(id = "chemistry", name = "Chemistry"),
            ScientificField(id = "neuroscience", name = "Neuroscience"),
            ScientificField(id = "earth_science", name = "Earth Science"),
            ScientificField(id = "astronomy", name = "Astronomy"),
            ScientificField(id = "mathematics", name = "Mathematics")
        )
    }
}
