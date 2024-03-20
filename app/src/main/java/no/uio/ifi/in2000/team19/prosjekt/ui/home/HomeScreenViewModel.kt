package no.uio.ifi.in2000.team19.prosjekt.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreenViewModel
import java.io.IOException


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


    private val _adviceUiState: MutableStateFlow<AdviceUiState> =
        MutableStateFlow(AdviceUiState.Loading)
    var adviceUiState: StateFlow<AdviceUiState> = _adviceUiState.asStateFlow()

    //private val _weatherForecastUiState: MutableStateFlow<WeatherForecastUiState> = MutableStateFlow(WeatherForecastUiState.Loading)
    //var weatherForecastUiState: StateFlow<WeatherForecastUiState> = _weatherForecastUiState.asStateFlow()

    // Temporary variables, to make testing easier
    private val latitude: String = "60"
    private val longitude: String = "10"
    private val height: String = "0"


    init {
        //loadAllAdvice()
        loadWeatherForecast()
        Log.d("vm", "VM done initializing")
    }

    private fun loadWeatherForecast() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherForecast = locationForecastRepository.getGeneralForecast(latitude, longitude, height, 3)
                //_weatherForecastUiState.value = WeatherForecastUiState.Success(weatherForecast)

                val allAdvice = locationForecastRepository.getAdvice(weatherForecast)
                _adviceUiState.value = AdviceUiState.Success(allAdvice, weatherForecast)
            } catch (e: IOException) {
                //WeatherForecastUiState.Error
                AdviceUiState.Error
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


    fun loadAllAdvice(latitude: String, longitude: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                //TODO change arguments later
                generalForecast =
                    locationForecastRepository.getGeneralForecast(latitude, longitude, "0", 10).toMutableList()

                val allAdvice = locationForecastRepository.getAdvice(generalForecast)
                _adviceUiState.value = AdviceUiState.Success(allAdvice)

            } catch (e: IOException) {
                AdviceUiState.Error
            }
        }
    }

}