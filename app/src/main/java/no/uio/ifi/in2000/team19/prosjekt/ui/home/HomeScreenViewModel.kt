package no.uio.ifi.in2000.team19.prosjekt.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepositoryInterface
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import java.io.IOException


sealed interface AdviceUiState{
    data class  Success(val allAdvice:List<Advice>) : AdviceUiState
    data object Loading : AdviceUiState
    data object Error : AdviceUiState
}

class HomeScreenViewModel: ViewModel() {

    private val locationForecastRepository: LocationForecastRepositoryInterface =
        LocationForecastRepository()
    private val _adviceUiState: MutableStateFlow<AdviceUiState> =
        MutableStateFlow(AdviceUiState.Loading)
    var adviceUiState: StateFlow<AdviceUiState> = _adviceUiState.asStateFlow()


    init {
        loadAllAdvice()
        Log.d("vm", "VM done initializing")
    }


    /*
    === Currently does not work, is not able to update adviceUiState ===
    fun reloadData(){
        _adviceUiState.value = AdviceUiState.Loading // Here to make sure UI knows its loading when also reloading the data, e.g by clicking home
        loadAllAdvice()
    }
     */

    private fun loadAllAdvice() {

        viewModelScope.launch(Dispatchers.IO) {
            Log.d("vm", "loading all advice...")
            try {
                //TODO change arguments later
                val allAdvice = locationForecastRepository.getAdvice("10", "60", "0")
                _adviceUiState.value = AdviceUiState.Success(allAdvice)

            } catch (e: IOException) {
                AdviceUiState.Error
            }
        }

    }
}