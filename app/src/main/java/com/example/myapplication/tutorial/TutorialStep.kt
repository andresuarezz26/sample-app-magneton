package com.example.myapplication.tutorial

data class TutorialStep(
    val title: String,
    val description: String,
    val emoji: String
)

val tutorialSteps = listOf(
    TutorialStep(
        title = "Welcome",
        description = "Swipe up and down to move through an endless feed of videos.",
        emoji = "👋"
    ),
    TutorialStep(
        title = "Like what you love",
        description = "Tap the heart to like a video and let creators know you enjoyed it.",
        emoji = "❤️"
    ),
    TutorialStep(
        title = "Comment & Share",
        description = "Join the conversation with comments, or share your favorite videos with friends.",
        emoji = "💬"
    ),
    TutorialStep(
        title = "You're all set",
        description = "That's everything you need to know. Enjoy exploring the feed!",
        emoji = "🎉"
    )
)
