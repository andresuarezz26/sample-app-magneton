package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore<Preferences> backing the onboarding gate. Exposed via a [Context.dataStore]
 * extension named `dataStore` so instrumentation tests can clear it directly.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding")

object OnboardingPreferences {
    val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
}

class OnboardingRepository(private val dataStore: DataStore<Preferences>) {

    val isOnboardingComplete: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[OnboardingPreferences.ONBOARDING_COMPLETE] ?: false
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { prefs ->
            prefs[OnboardingPreferences.ONBOARDING_COMPLETE] = complete
        }
    }
}
