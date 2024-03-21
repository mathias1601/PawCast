package no.uio.ifi.in2000.team19.prosjekt.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast


sealed interface AdviceUiState{
    data class  Success(val allAdvice:List<Advice>, val weatherForecast: List<GeneralForecast>) : AdviceUiState
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


class HomeScreenViewModel: ViewModel() {

    private val locationForecastRepository =
        LocationForecastRepository()
    private lateinit var settingsRepository : SettingsRepository


    private val _adviceUiState: MutableStateFlow<AdviceUiState> =
        MutableStateFlow(AdviceUiState.Loading)
    var adviceUiState: StateFlow<AdviceUiState> = _adviceUiState.asStateFlow()

    //private val _weatherForecastUiState: MutableStateFlow<WeatherForecastUiState> = MutableStateFlow(WeatherForecastUiState.Loading)
    //var weatherForecastUiState: StateFlow<WeatherForecastUiState> = _weatherForecastUiState.asStateFlow()

    private val height: String = "0"
    lateinit var cords: Cords

    fun initialize(repository: SettingsRepository) {
        viewModelScope.launch(Dispatchers.IO) {

            settingsRepository = repository
            cords = settingsRepository.getCoords()
            loadWeatherForecast()
        }
    }

    fun loadWeatherForecast() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherForecast = locationForecastRepository.getGeneralForecast(cords.latitude, cords.latitude, height, 3)
                val allAdvice = locationForecastRepository.getAdvice(weatherForecast)
                cords = settingsRepository.getCoords()
                _adviceUiState.value = AdviceUiState.Success(allAdvice, weatherForecast)
            } catch (e: Exception) {
               _adviceUiState.value = AdviceUiState.Error
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