package com.example.myapplication.onboarding

sealed interface OnboardingIntent {
    data object LoadFields : OnboardingIntent
    data class ToggleField(val fieldId: String) : OnboardingIntent
    data class UpdateEmail(val email: String) : OnboardingIntent
}
