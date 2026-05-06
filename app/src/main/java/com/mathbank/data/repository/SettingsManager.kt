package com.mathbank.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        val KEY_OPENROUTER_KEY = stringPreferencesKey("openrouter_api_key")
        val KEY_GEMINI_KEY = stringPreferencesKey("gemini_api_key")
        val KEY_SELECTED_MODEL = stringPreferencesKey("selected_model")
        val KEY_DEFAULT_QUESTION_COUNT = intPreferencesKey("default_question_count")
        val KEY_DEFAULT_TIME_LIMIT = intPreferencesKey("default_time_limit")
        const val MODEL_OPENROUTER = "openrouter"
        const val MODEL_GEMINI = "gemini"
    }

    suspend fun getOpenRouterKey(): String =
        context.dataStore.data.first()[KEY_OPENROUTER_KEY] ?: ""

    suspend fun saveOpenRouterKey(key: String) {
        context.dataStore.edit { it[KEY_OPENROUTER_KEY] = key }
    }

    suspend fun getGeminiKey(): String =
        context.dataStore.data.first()[KEY_GEMINI_KEY] ?: ""

    suspend fun saveGeminiKey(key: String) {
        context.dataStore.edit { it[KEY_GEMINI_KEY] = key }
    }

    suspend fun getSelectedModel(): String =
        context.dataStore.data.first()[KEY_SELECTED_MODEL] ?: MODEL_OPENROUTER

    suspend fun saveSelectedModel(model: String) {
        context.dataStore.edit { it[KEY_SELECTED_MODEL] = model }
    }

    suspend fun getActiveApiKey(): String {
        return when (getSelectedModel()) {
            MODEL_GEMINI -> getGeminiKey()
            else -> getOpenRouterKey()
        }
    }

    suspend fun getApiKey(): String = getActiveApiKey()
    suspend fun saveApiKey(key: String) = saveOpenRouterKey(key)
    suspend fun isApiKeySet(): Boolean = getActiveApiKey().isNotEmpty()

    suspend fun getDefaultQuestionCount(): Int =
        context.dataStore.data.first()[KEY_DEFAULT_QUESTION_COUNT] ?: 20

    suspend fun getDefaultTimeLimit(): Int =
        context.dataStore.data.first()[KEY_DEFAULT_TIME_LIMIT] ?: 0
}
