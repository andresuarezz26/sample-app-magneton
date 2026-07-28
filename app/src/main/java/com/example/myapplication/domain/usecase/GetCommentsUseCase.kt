package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetCommentsUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(videoId: String): List<Comment> = withContext(ioDispatcher) {
        listOf(
            Comment(
                id = "1",
                videoId = videoId,
                author = "@spacefan22",
                text = "This blew my mind, had no idea black holes could do that!",
                likes = 342,
                timestampLabel = "2h ago"
            ),
            Comment(
                id = "2",
                videoId = videoId,
                author = "@curious_cat",
                text = "Wait so does that mean black holes eventually disappear?",
                likes = 128,
                timestampLabel = "3h ago"
            ),
            Comment(
                id = "3",
                videoId = videoId,
                author = "@physics_nerd",
                text = "Hawking radiation is such an elegant idea honestly",
                likes = 951,
                timestampLabel = "5h ago"
            ),
            Comment(
                id = "4",
                videoId = videoId,
                author = "@newtoallthis",
                text = "Can someone explain this like I'm five lol",
                likes = 76,
                timestampLabel = "6h ago"
            ),
            Comment(
                id = "5",
                videoId = videoId,
                author = "@stargazer99",
                text = "Following this account for more content like this!",
                likes = 214,
                timestampLabel = "9h ago"
            ),
            Comment(
                id = "6",
                videoId = videoId,
                author = "@quantum_queen",
                text = "The universe really said 'hold my beer' with this one",
                likes = 1204,
                timestampLabel = "12h ago"
            ),
            Comment(
                id = "7",
                videoId = videoId,
                author = "@night_owl",
                text = "Watched this three times in a row, so good",
                likes = 58,
                timestampLabel = "1d ago"
            )
        )
    }
}
