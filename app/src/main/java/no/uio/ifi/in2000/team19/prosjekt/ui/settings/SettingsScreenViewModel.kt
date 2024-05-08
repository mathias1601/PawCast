package no.uio.ifi.in2000.team19.prosjekt.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.dataStore.DataStoreRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _cordsUiState: MutableStateFlow<Cords> = MutableStateFlow(Cords(0, "default", "default", "12", "34"))
    val cordsUiState: StateFlow<Cords> = _cordsUiState.asStateFlow()

    private var isInitialized = false

    init {
        Log.d("SettingScreenViewModel", "Running init{}. Is initialized: $isInitialized")
        isInitialized = true
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.getCords().collect {
                _cordsUiState.value = it
                }
        }
    }

    fun clearDataStore(){
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.clearDataStore()
        }
    }
}
