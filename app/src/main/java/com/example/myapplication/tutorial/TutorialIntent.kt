package com.example.myapplication.tutorial

sealed interface TutorialIntent {
    data object NextPage : TutorialIntent
    data object PreviousPage : TutorialIntent
    data object SkipTutorial : TutorialIntent
    data object CompleteTutorial : TutorialIntent
}
