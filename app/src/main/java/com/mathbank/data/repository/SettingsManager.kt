package com.mathbank.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        val KEY_API_KEY = stringPreferencesKey("claude_api_key")
        val KEY_DEFAULT_QUESTION_COUNT = intPreferencesKey("default_question_count")
        val KEY_DEFAULT_TIME_LIMIT = intPreferencesKey("default_time_limit")
        val KEY_SKIP_TITLE_PAGES = booleanPreferencesKey("skip_title_pages")
        val KEY_IMAGE_QUALITY = intPreferencesKey("image_quality")
        val KEY_AUTO_DETECT_ANSWERS = booleanPreferencesKey("auto_detect_answers")
    }

    val apiKeyFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[KEY_API_KEY] ?: "" }

    suspend fun getApiKey(): String =
        context.dataStore.data.first()[KEY_API_KEY] ?: ""

    suspend fun saveApiKey(key: String) {
        context.dataStore.edit { it[KEY_API_KEY] = key }
    }

    suspend fun getDefaultQuestionCount(): Int =
        context.dataStore.data.first()[KEY_DEFAULT_QUESTION_COUNT] ?: 20

    suspend fun saveDefaultQuestionCount(count: Int) {
        context.dataStore.edit { it[KEY_DEFAULT_QUESTION_COUNT] = count }
    }

    suspend fun getDefaultTimeLimit(): Int =
        context.dataStore.data.first()[KEY_DEFAULT_TIME_LIMIT] ?: 0

    suspend fun saveDefaultTimeLimit(minutes: Int) {
        context.dataStore.edit { it[KEY_DEFAULT_TIME_LIMIT] = minutes }
    }

    suspend fun isApiKeySet(): Boolean = getApiKey().isNotEmpty()
}
