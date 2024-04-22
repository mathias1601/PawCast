package no.uio.ifi.in2000.team19.prosjekt.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "setup_pref")

class DataStoreRepository (context: Context){


    private object PreferancesKey {
        val setupStateKey = booleanPreferencesKey(name ="isSetupCompleted")
    }

    private val dataStore = context.dataStore

    suspend fun saveSetupState(isCompleted : Boolean){
        dataStore.edit { preferances ->
            preferances[PreferancesKey.setupStateKey] = isCompleted
        }
    }

    fun readSetupState(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            val setupState = preferences[PreferancesKey.setupStateKey] ?: false // :? betyr bare return false vis alt til venstre blir null
            setupState
        }
    }

    suspend fun clearDataStore(){
        dataStore.edit {
            it.clear()
        }
    }
}