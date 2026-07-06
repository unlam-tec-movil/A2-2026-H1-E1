package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenManager
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        val tokenFlow: Flow<String> = dataStore.data.map { preferences -> preferences[TOKEN_KEY] ?: "" }

        companion object {
            val TOKEN_KEY = stringPreferencesKey("jwt_token")
        }

        suspend fun saveToken(token: String) {
            dataStore.edit { preferences ->

                preferences[TOKEN_KEY] = token
            }
        }

        suspend fun clearToken() {
            dataStore.edit { preferences ->
                preferences.remove(TOKEN_KEY)
            }
        }
    }
