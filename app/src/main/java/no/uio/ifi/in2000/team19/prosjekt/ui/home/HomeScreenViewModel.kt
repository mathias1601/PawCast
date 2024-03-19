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
    data class  Success(val allAdvice:List<Advice>) : AdviceUiState
    data object Loading : AdviceUiState
    data object Error : AdviceUiState
}

class HomeScreenViewModel: ViewModel() {


    private val locationForecastRepository =
        LocationForecastRepository()
        

    private var generalForecast: MutableList<GeneralForecast> = mutableListOf()


    private val _adviceUiState: MutableStateFlow<AdviceUiState> =
        MutableStateFlow(AdviceUiState.Loading)
    var adviceUiState: StateFlow<AdviceUiState> = _adviceUiState.asStateFlow()



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