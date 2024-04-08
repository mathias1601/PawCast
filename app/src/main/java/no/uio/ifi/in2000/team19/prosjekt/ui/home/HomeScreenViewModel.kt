package no.uio.ifi.in2000.team19.prosjekt.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsDatabase
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import java.io.IOException


sealed interface AdviceUiState{
    data class  Success(val allAdvice:List<Advice>) : AdviceUiState
    data object Loading : AdviceUiState
    data object Error : AdviceUiState
}

/*
sealed interface WeatherForecastUiState {
    data class Success(val weatherForecast: List<GeneralForecast>): WeatherForecastUiState
    data object Loading: WeatherForecastUiState
    data object Error: WeatherForecastUiState
}
 */


class HomeScreenViewModel(application: Application): AndroidViewModel(application) {



    private val locationForecastRepository = LocationForecastRepository()

    private val _adviceUiState: MutableStateFlow<AdviceUiState> = MutableStateFlow(AdviceUiState.Loading)
    var adviceUiState: StateFlow<AdviceUiState> = _adviceUiState.asStateFlow()

    private val coordsDao = SettingsDatabase.getDatabase(application).coordsDao()
    private val settingsRepository = SettingsRepository(coordsDao)

    private var _cordsUiState:MutableStateFlow<Cords> = MutableStateFlow(Cords(0, "69", "69"))
    var cordsUiState: StateFlow<Cords> = _cordsUiState.asStateFlow()

    private val height: String = "0"

    init {
        loadWeatherForecast()
        viewModelScope.launch(Dispatchers.IO) {
            _cordsUiState.value = settingsRepository.getCords()
        }

    }

    fun loadWeatherForecast() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cords = settingsRepository.getCords()
                Log.d("HSVM", cords.latitude + cords.longitude)
                val weatherForecast = locationForecastRepository.getGeneralForecast(cords.latitude, cords.longitude, height, 3)
                val allAdvice = locationForecastRepository.getAdvice(weatherForecast)
                _adviceUiState.value = AdviceUiState.Success(allAdvice)
            } catch (e: IOException) {
               Log.d("HSVM", e.toString())
                _adviceUiState.value  = AdviceUiState.Error
            }
        }
    }

    /*
    === Currently does not work, is not able to update adviceUiState ===

    * Might be that _adviceUiState is a val / not a var

    fun reloadData(){
        _adviceUiState.value = AdviceUiState.Loading // Here to make sure UI knows its loading when also reloading the data, e.g by clicking home
        loadAllAdvice()
    }
     */

}