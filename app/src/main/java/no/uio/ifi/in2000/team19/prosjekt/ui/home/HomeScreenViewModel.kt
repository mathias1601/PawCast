package no.uio.ifi.in2000.team19.prosjekt.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.ExtraStore
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
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadWeatherForecast() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cords = settingsRepository.getCords()
                _cordsUiState.value = cords

                val generalForecast = locationForecastRepository.getGeneralForecast(cords.latitude, cords.longitude, "0", 3)
                val allAdvice = locationForecastRepository.getAdvice(generalForecast)

                _adviceUiState.value = AdviceUiState.Success(allAdvice)

                /////////////////////////////////// GRAPH METHOD TO BE MOVED INTO REPOSITORY OR NEW DOMAIN LAYER///////////////////////////////////

                val data =
                    mapOf(
                        LocalDate.parse("2022-07-01") to 2f,
                        LocalDate.parse("2022-07-02") to 6f,
                        LocalDate.parse("2022-07-04") to 4f,
                    )
                val xToDateMapKey = ExtraStore.Key<Map<Float, LocalDate>>()




                val xToDates = data.keys.associateBy { it.toEpochDay().toFloat() }
                _graphUiState.value.tryRunTransaction {
                    lineSeries { series(xToDates.keys, data.values) }
                    updateExtras { it[xToDateMapKey] = xToDates }
                }

                val dateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")
                AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, chartValues, _ ->
                    (chartValues.model.extraStore[xToDateMapKey][x] ?: LocalDate.ofEpochDay(x.toLong()))
                        .format(dateTimeFormatter)
                }




            } catch (e: IOException) {
                _adviceUiState.value  = AdviceUiState.Error
            }
        }
    }
}