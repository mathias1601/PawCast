package no.uio.ifi.in2000.team19.prosjekt.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsDatabase
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords

class SettingsScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SettingsRepository
    private val _coordinates: MutableStateFlow<Cords> = MutableStateFlow(Cords(0, "", ""))
    val coordinates = _coordinates.asStateFlow()

    init {
        val coordsDao = SettingsDatabase.getDatabase(application).coordsDao()
        repository = SettingsRepository(coordsDao)

        viewModelScope.launch {
            _coordinates.value = repository.getCoords()
        }
    }

    fun setLatitude(newLatitude:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCoords(coordinates.value.longitude, newLatitude)
        }
    }

    fun setLongitude(newLongitude:String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCoords(newLongitude, coordinates.value.latitude)
        }
    }
}