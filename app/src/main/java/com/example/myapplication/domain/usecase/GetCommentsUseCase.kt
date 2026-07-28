package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetCommentsUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(): List<Comment> = withContext(ioDispatcher) {
        listOf(
            Comment(
                id = "1",
                username = "ana.codes",
                text = "This is amazing!",
                timestampLabel = "2h",
                likes = 14
            ),
            Comment(
                id = "2",
                username = "maria_dev",
                text = "Totally agree with this take",
                timestampLabel = "5h",
                likes = 3
            ),
            Comment(
                id = "3",
                username = "pedro_ok",
                text = "lol",
                timestampLabel = "1d",
                likes = 0
            ),
            Comment(
                id = "4",
                username = "quantum_lab",
                text = "Wait, this changes everything I thought I knew",
                timestampLabel = "1d",
                likes = 42
            ),
            Comment(
                id = "5",
                username = "bio_wonders",
                text = "Source? Would love to read more about this",
                timestampLabel = "2d",
                likes = 7
            ),
            Comment(
                id = "6",
                username = "climate_watch",
                text = "Sharing this with my class tomorrow",
                timestampLabel = "3d",
                likes = 21
            ),
            Comment(
                id = "7",
                username = "neuro_fan",
                text = "First!",
                timestampLabel = "4d",
                likes = 1
            ),
            Comment(
                id = "8",
                username = "chem_curious",
                text = "Not sure I agree, but interesting perspective",
                timestampLabel = "5d",
                likes = 9
            )
        )
    }
}
