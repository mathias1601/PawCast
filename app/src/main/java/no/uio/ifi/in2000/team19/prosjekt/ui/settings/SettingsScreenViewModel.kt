package no.uio.ifi.in2000.team19.prosjekt.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsDatabase
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords

class SettingsScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val coordsDao = SettingsDatabase.getDatabase(application).coordsDao()
    private val settingsRepository = SettingsRepository(coordsDao)

    private val _cordsUiState:MutableStateFlow<Cords> = MutableStateFlow(Cords(0, "12", "34"))
    val cordsUiState: StateFlow<Cords> = _cordsUiState.asStateFlow()

    init {
        viewModelScope.launch (Dispatchers.IO) {
            _cordsUiState.value = settingsRepository.getCords()
        }
    }
    fun setCoordinates(newLatitude:String, newLongitude: String){
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateCoords( newLatitude, newLongitude)
            _cordsUiState.value = settingsRepository.getCords()
        }
    }
}