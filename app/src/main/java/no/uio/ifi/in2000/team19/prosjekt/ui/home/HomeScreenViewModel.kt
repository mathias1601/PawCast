package no.uio.ifi.in2000.team19.prosjekt.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsDatabase
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import java.io.IOException
import javax.inject.Inject


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

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val locationForecastRepository: LocationForecastRepository
): ViewModel() {

    private var _adviceUiState: MutableStateFlow<AdviceUiState> = MutableStateFlow(AdviceUiState.Loading)
    var adviceUiState: StateFlow<AdviceUiState> = _adviceUiState.asStateFlow()

    private var _cordsUiState:MutableStateFlow<Cords> = MutableStateFlow(Cords(0, "69", "69"))
    var cordsUiState: StateFlow<Cords> = _cordsUiState.asStateFlow()

    private val height: String = "0"

    /*init {
        loadWeatherForecast()

    }*/

    fun loadWeatherForecast() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cords = settingsRepository.getCords()
                _cordsUiState.value = cords

                val weatherForecast = locationForecastRepository.getGeneralForecast(cords.latitude, cords.longitude, height, 3)
                val allAdvice = locationForecastRepository.getAdvice(weatherForecast)
                _adviceUiState.value = AdviceUiState.Success(allAdvice, weatherForecast)
            } catch (e: IOException) {
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