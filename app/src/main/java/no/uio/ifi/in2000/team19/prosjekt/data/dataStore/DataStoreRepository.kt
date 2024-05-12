package no.uio.ifi.in2000.team19.prosjekt.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "setup_pref")

/** Used to store a Boolean saying if the user has completed setup.
 *
 *  Reading from database is too slow to do at app launch, so we decided
 *  on using a simple dataStore instead for storing a Boolean if the user has setup their profile.
 *
 *  This is updated at the end of the setup process OR end of skip process.
 *
 * */
class DataStoreRepository(context: Context) { // Context is provided by Dagger Hilt.


    private object PreferancesKey {
        val setupStateKey = booleanPreferencesKey(name = "isSetupCompleted")
    }

    private val dataStore = context.dataStore

    suspend fun saveSetupState(isCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferancesKey.setupStateKey] = isCompleted
        }
    }

    fun readSetupState(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            val setupState = preferences[PreferancesKey.setupStateKey]
                ?: false // return false if nothing is stored
            setupState
        }
    }
}