package com.example.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "elenio_user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_IS_PRO = booleanPreferencesKey("is_pro_user")
        val KEY_HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val KEY_IS_DEMO_ENABLED = booleanPreferencesKey("is_demo_enabled")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode") // "DARK", "LIGHT", "SYSTEM"
        val KEY_LANGUAGE = stringPreferencesKey("language") // "ID", "EN"
        val KEY_KEEP_SCREEN_AWAKE = booleanPreferencesKey("keep_screen_awake")
        val KEY_AUTO_RECONNECT = booleanPreferencesKey("auto_reconnect")
        val KEY_DEFAULT_STREAM_QUALITY = stringPreferencesKey("default_stream_quality") // "HD", "SD"
        val KEY_GRID_LAYOUT_MODE = intPreferencesKey("grid_layout_mode") // 1 (1x1), 2 (2x2), 3 (3x3)
    }

    val isProUser: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_PRO] ?: false }
    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { it[KEY_HAS_COMPLETED_ONBOARDING] ?: false }
    val isDemoEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_DEMO_ENABLED] ?: true }
    val themeMode: Flow<String> = context.dataStore.data.map { it[KEY_THEME_MODE] ?: "DARK" }
    val language: Flow<String> = context.dataStore.data.map { it[KEY_LANGUAGE] ?: "ID" }
    val keepScreenAwake: Flow<Boolean> = context.dataStore.data.map { it[KEY_KEEP_SCREEN_AWAKE] ?: true }
    val autoReconnect: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTO_RECONNECT] ?: true }
    val defaultStreamQuality: Flow<String> = context.dataStore.data.map { it[KEY_DEFAULT_STREAM_QUALITY] ?: "HD" }
    val gridLayoutMode: Flow<Int> = context.dataStore.data.map { it[KEY_GRID_LAYOUT_MODE] ?: 2 }

    suspend fun setProUser(isPro: Boolean) {
        context.dataStore.edit { it[KEY_IS_PRO] = isPro }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_HAS_COMPLETED_ONBOARDING] = completed }
    }

    suspend fun setDemoEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_IS_DEMO_ENABLED] = enabled }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = lang }
    }

    suspend fun setKeepScreenAwake(keepAwake: Boolean) {
        context.dataStore.edit { it[KEY_KEEP_SCREEN_AWAKE] = keepAwake }
    }

    suspend fun setAutoReconnect(autoReconnect: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_RECONNECT] = autoReconnect }
    }

    suspend fun setDefaultStreamQuality(quality: String) {
        context.dataStore.edit { it[KEY_DEFAULT_STREAM_QUALITY] = quality }
    }

    suspend fun setGridLayoutMode(mode: Int) {
        context.dataStore.edit { it[KEY_GRID_LAYOUT_MODE] = mode }
    }
}
