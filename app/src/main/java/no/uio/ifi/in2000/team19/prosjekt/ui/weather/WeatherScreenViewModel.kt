package no.uio.ifi.in2000.team19.prosjekt.ui.weather

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay
import java.io.IOException
import javax.inject.Inject

sealed interface WeatherUiState {
    data class Success(
        val weatherHours: List<GeneralForecast>,
        val weatherDays: List<WeatherForDay>): WeatherUiState
    data object Loading: WeatherUiState
    data object Error: WeatherUiState
}

@RequiresApi(Build.VERSION_CODES.O)
class WeatherScreenViewModel @Inject constructor(
    private val locationForecastRepository : LocationForecastRepository
) : ViewModel() {


    //private var weatherUiState: WeatherUiState by mutableStateOf(WeatherUiState.Loading)

    private val _weatherUiState: MutableStateFlow<WeatherUiState> = MutableStateFlow(WeatherUiState.Loading)
    var weatherUiState: StateFlow<WeatherUiState> = _weatherUiState.asStateFlow()

    private val longitude: String = "10"
    private val latitude: String = "60"
    private val height: String = "0"

    init {
        loadWeather()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadWeather() {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val weatherHoursDeferred = async {locationForecastRepository.getGeneralForecast(latitude, longitude, height, 3)}
                Log.d("Debug", "Loader vær for timer")


                val weatherDaysDeferred = async {locationForecastRepository.getGeneralForecastForDays(latitude,longitude, height, 2)}
                Log.d("Debug", "Loader vær for dager")

                val weatherHours = weatherHoursDeferred.await()
                val weatherDays = weatherDaysDeferred.await()
                //weatherUiState = WeatherUiState.Success(weatherHours, weatherDays)
                _weatherUiState.value = WeatherUiState.Success(weatherHours, weatherDays)
            } catch (e: IOException) {
                _weatherUiState.value = WeatherUiState.Error
            }
        }
    }

    //fun fetchWeatherUiState(): WeatherUiState = weatherUiState

}