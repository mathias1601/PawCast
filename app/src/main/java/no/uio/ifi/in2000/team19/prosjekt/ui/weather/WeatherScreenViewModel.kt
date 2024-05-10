package no.uio.ifi.in2000.team19.prosjekt.ui.weather

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
import no.uio.ifi.in2000.team19.prosjekt.model.ErrorReasons
import java.io.IOException
import java.nio.channels.UnresolvedAddressException
import javax.inject.Inject


sealed interface WeatherUiState {
    data class Success(val weather: ForecastTypes): WeatherUiState
    data object Loading: WeatherUiState
    data class Error(val errorReason : ErrorReasons): WeatherUiState
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
                    _locationUiState.value = cords
                    loadWeather()
                }

        }
    }

    fun loadWeather() {

        val cords = _locationUiState.value
        _weatherUiState.value = WeatherUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherForecast = locationForecastRepository.getGeneralForecast(cords.latitude, cords.longitude, "0", 2)
                _weatherUiState.value = WeatherUiState.Success(weatherForecast)

               // See similar try-catch in HomeScreenViewModel for explanation of connection handling.
            } catch (e: IOException) {
                _weatherUiState.value = WeatherUiState.Error(ErrorReasons.INTERRUPTION)
            } catch (e: UnresolvedAddressException){
                _weatherUiState.value = WeatherUiState.Error(ErrorReasons.INTERNET)
            } catch (e: Exception){
                _weatherUiState.value = WeatherUiState.Error(ErrorReasons.UNKNOWN)
            }

        }
    }

    fun checkIfUiStateIsError(): Boolean {
        return _weatherUiState.value is WeatherUiState.Error
    }
}
