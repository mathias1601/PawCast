package no.uio.ifi.in2000.team19.prosjekt.ui.weather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.ForecastTypes
import java.io.IOException
import javax.inject.Inject


sealed interface WeatherUiState {
    data class Success(
        val weather: ForecastTypes): WeatherUiState

    data object Loading: WeatherUiState
    data object Error: WeatherUiState
}

@HiltViewModel
class WeatherScreenViewModel @Inject constructor(
    private val locationForecastRepository: LocationForecastRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {


    //private var weatherUiState: WeatherUiState by mutableStateOf(WeatherUiState.Loading)

    private val _weatherUiState: MutableStateFlow<WeatherUiState> =
        MutableStateFlow(WeatherUiState.Loading)
    var weatherUiState: StateFlow<WeatherUiState> = _weatherUiState.asStateFlow()

    private var _locationUiState:MutableStateFlow<Cords> = MutableStateFlow(Cords(0, "default", "default", "69", "69"))
    var locationUiState: StateFlow<Cords> = _locationUiState.asStateFlow()


    init {

        viewModelScope.launch(Dispatchers.IO) {
                settingsRepository.getCords().collect {cords ->
                    loadWeather(cords)
                    _locationUiState.value = cords
                }

        }
    }

    private fun loadWeather(cords: Cords) {
        viewModelScope.launch(Dispatchers.IO) {

            try {

                Log.d("WeatherViewModel", "kaller på getGeneralForecast...")

                val weatherForecast = locationForecastRepository.getGeneralForecast(cords.latitude, cords.longitude, "0", 2)

                _weatherUiState.value = WeatherUiState.Success(weatherForecast)
            } catch (e: IOException) {
                _weatherUiState.value = WeatherUiState.Error
            }
        }
    }
}
