package no.uio.ifi.in2000.team19.prosjekt.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords

class SettingsScreenViewModel() : ViewModel() {

    private val _coordinates: MutableStateFlow<Cords> = MutableStateFlow(Cords(0, "", ""))
    val coordinates = _coordinates.asStateFlow()
    private lateinit var settingsRepository : SettingsRepository

    fun initialize(repository: SettingsRepository) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository = repository
            _coordinates.value = settingsRepository.getCoords()
        }
    }



    fun setLatitude(newLatitude:String){
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateCoords( newLatitude, coordinates.value.longitude)
            coordinates.value.latitude = newLatitude
        }
    }

    fun setLongitude(newLongitude:String) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateCoords(coordinates.value.latitude, newLongitude)
            coordinates.value.longitude = newLongitude
        }
    }
}