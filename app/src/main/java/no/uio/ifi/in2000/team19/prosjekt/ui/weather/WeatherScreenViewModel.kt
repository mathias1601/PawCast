package no.uio.ifi.in2000.team19.prosjekt.ui.weather

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.forecastSuper
import java.io.IOException
import javax.inject.Inject

/*
sealed interface WeatherUiState {
    data class Success(
        val weather: List<List<forecastSuper>>) : WeatherUiState

    data object Loading: WeatherUiState
    data object Error: WeatherUiState
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class WeatherScreenViewModel @Inject constructor(
    private val locationForecastRepository : LocationForecastRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {


    //private var weatherUiState: WeatherUiState by mutableStateOf(WeatherUiState.Loading)

    private val _weatherUiState: MutableStateFlow<WeatherUiState> = MutableStateFlow(WeatherUiState.Loading)
    var weatherUiState: StateFlow<WeatherUiState> = _weatherUiState.asStateFlow()

    init {
        loadWeather()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadWeather() {
        viewModelScope.launch(Dispatchers.IO) {

            val cords = settingsRepository.getCords()

            try {
                val weatherForecast = locationForecastRepository.getGeneralForecast(cords.latitude, cords.latitude, "0", 3, 2)
                Log.d("Debug", "Loader vær for timer")


                //val weatherDaysDeferred = async {locationForecastRepository.getGeneralForecastForDays(cords.latitude,cords.longitude, "0", 2)}
                //Log.d("Debug", "Loader vær for dager")


                //weatherUiState = WeatherUiState.Success(weatherHours, weatherDays)
                _weatherUiState.value = WeatherUiState.Success(weatherForecast)
            } catch (e: IOException) {
                _weatherUiState.value = WeatherUiState.Error
            }
        }
    }

    //fun fetchWeatherUiState(): WeatherUiState = weatherUiState

}


 */
