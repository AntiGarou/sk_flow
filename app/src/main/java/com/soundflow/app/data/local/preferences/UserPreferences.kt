package com.soundflow.app.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "soundflow_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_AUDIO_QUALITY = stringPreferencesKey("audio_quality")
        val KEY_DOWNLOAD_QUALITY = stringPreferencesKey("download_quality")
        val KEY_THEME = stringPreferencesKey("theme")
    }

    val audioQuality: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUDIO_QUALITY] ?: "mp32"
    }

    val downloadQuality: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_DOWNLOAD_QUALITY] ?: "mp32"
    }

    suspend fun setAudioQuality(quality: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AUDIO_QUALITY] = quality
        }
    }

    suspend fun setDownloadQuality(quality: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DOWNLOAD_QUALITY] = quality
        }
    }
}
