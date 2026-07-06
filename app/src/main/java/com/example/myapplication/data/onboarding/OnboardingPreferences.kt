package com.example.myapplication.data.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_preferences")

interface OnboardingPreferencesRepository {
    val isOnboardingComplete: Flow<Boolean>
    suspend fun setComplete()
}

class OnboardingPreferences(private val context: Context) : OnboardingPreferencesRepository {

    private val onboardingCompleteKey = booleanPreferencesKey("onboarding_complete")

    override val isOnboardingComplete: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[onboardingCompleteKey] ?: false
    }

    override suspend fun setComplete() {
        context.dataStore.edit { preferences ->
            preferences[onboardingCompleteKey] = true
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
