package no.uio.ifi.in2000.team19.prosjekt.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import java.io.IOException
import javax.inject.Inject


sealed interface AdviceUiState{
    data class  Success(val allAdvice:List<Advice>) : AdviceUiState
    data object Loading : AdviceUiState
    data object Error : AdviceUiState
}


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val locationForecastRepository: LocationForecastRepository
): ViewModel() {

    private val _graphUiState = MutableStateFlow(CartesianChartModelProducer.build())
    var graphUiState: StateFlow<CartesianChartModelProducer> = _graphUiState.asStateFlow()

    private var _adviceUiState: MutableStateFlow<AdviceUiState> = MutableStateFlow(AdviceUiState.Loading)
    var adviceUiState: StateFlow<AdviceUiState> = _adviceUiState.asStateFlow()

    private var _cordsUiState:MutableStateFlow<Cords> = MutableStateFlow(Cords(0, "69", "69"))
    var cordsUiState: StateFlow<Cords> = _cordsUiState.asStateFlow()

    private var _userInfoUiState:MutableStateFlow<UserInfo> = MutableStateFlow(UserInfo(0, "loading", "loading", listOf()))
    var userInfoUiState: StateFlow<UserInfo> = _userInfoUiState.asStateFlow()

    fun loadWeatherForecast() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cords = settingsRepository.getCords()
                _cordsUiState.value = cords

                val generalForecast = locationForecastRepository.getGeneralForecast(cords.latitude, cords.longitude, "0", 3)
                val allAdvice = locationForecastRepository.getAdvice(generalForecast)

                _adviceUiState.value = AdviceUiState.Success(allAdvice)

                /////////////////////////////////// GRAPH METHOD TO BE MOVED INTO REPOSITORY OR NEW DOMAIN LAYER///////////////////////////////////

                val x = listOf(3, 5, 6)
                val y = listOf(10, 4, 5)

                _graphUiState.value.tryRunTransaction {
                    lineSeries {
                        series(
                        x=x,
                        y=y) }
                }




            } catch (e: IOException) {
                _adviceUiState.value  = AdviceUiState.Error
            }
        }
    }
}