package com.example.myapplication.onboarding

sealed interface OnboardingIntent {
    data object LoadFields : OnboardingIntent
    data class ToggleField(val fieldId: String) : OnboardingIntent
    data class EmailChanged(val email: String) : OnboardingIntent
    data object SubmitEmail : OnboardingIntent
    data object SkipEmail : OnboardingIntent
}
