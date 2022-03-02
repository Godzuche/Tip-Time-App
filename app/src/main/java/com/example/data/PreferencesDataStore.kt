package com.example.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


private const val INPUT_PREFERENCES_NAME = "input_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = INPUT_PREFERENCES_NAME
)

class PreferencesDataStore(context: Context) {

    // Save input preferences
    suspend fun saveInputPreferences(tipOption: Int,isRoundUp: Boolean, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[TIP_OPTION] = tipOption
            preferences[IS_ROUND_UP] = isRoundUp
        }
    }

    val preferencesFlow: Flow<Preferences> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }

    companion object {
        // Int preference key for the tip options
        val TIP_OPTION = intPreferencesKey("tip_option")
        // Boolean preference key for the round up switch
        val IS_ROUND_UP = booleanPreferencesKey("is_round_up")
    }
}