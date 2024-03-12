package no.uio.ifi.in2000.team19.prosjekt.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepositoryInterface
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import java.io.IOException

// ui states

// metoder for å loade data inn i ui state / oppdatere dem

sealed interface AdviceUiState{
    data class Success(val allAdvice:List<Advice>) : AdviceUiState
    data object Loading : AdviceUiState
    data object Error : AdviceUiState
}

class HomeScreenViewModel: ViewModel(){

    private val locationForecastRepository : LocationForecastRepositoryInterface = LocationForecastRepository()

    private var adviceUiState: AdviceUiState by mutableStateOf(AdviceUiState.Loading)


    init {
        loadAllAdvice()
    }

   private fun loadAllAdvice() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //TODO change arguments later
                val allAdvice = locationForecastRepository.getAdvice("10", "60")
                adviceUiState = AdviceUiState.Success(allAdvice)
            }
            catch (e: IOException) {
                AdviceUiState.Error
            }
            //} catch (e: HttpException) {
            //                AlpacaUiState.Error
            //            }
        }

    }

    fun getAdviceUiState(): AdviceUiState {
        return adviceUiState
    }

    /*
    *
    *  Holder på LocationForecastRepository
    *  - AdviceUiState som holder på List<Advice>
    *  -
    *
    * */
}