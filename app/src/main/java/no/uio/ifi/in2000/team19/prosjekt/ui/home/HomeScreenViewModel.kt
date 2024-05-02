package no.uio.ifi.in2000.team19.prosjekt.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import java.io.IOException
import javax.inject.Inject


sealed interface AdviceUiState{
    data class  Success(val allAdvice:List<Advice>) : AdviceUiState
    data object Loading : AdviceUiState
    data object Error : AdviceUiState
}


@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val locationForecastRepository: LocationForecastRepository
): ViewModel() {

    private var _adviceUiState: MutableStateFlow<AdviceUiState> = MutableStateFlow(AdviceUiState.Loading)
    var adviceUiState: StateFlow<AdviceUiState> = _adviceUiState.asStateFlow()

    private val _graphUiState = MutableStateFlow(CartesianChartModelProducer.build())
    var graphUiState: StateFlow<CartesianChartModelProducer> = _graphUiState.asStateFlow()

    private var _cordsUiState:MutableStateFlow<Cords> = MutableStateFlow(Cords(0, "default", "default", "69", "69"))
    var cordsUiState: StateFlow<Cords> = _cordsUiState.asStateFlow()

    //Kommer mby ikke til å bruke dette
    private var _userInfoUiState:MutableStateFlow<UserInfo> = MutableStateFlow(UserInfo(0, "loading", "loading", false, false, false, false, false, false, false, false, false, false))
    var userInfoUiState: StateFlow<UserInfo> = _userInfoUiState.asStateFlow()

    private val height: String = "0"

    private lateinit var adviceList: List<Advice>

    init {
        loadWeatherForecast()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadWeatherForecast() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cords = settingsRepository.getCords()
                _cordsUiState.value = cords

                val userInfo = settingsRepository.getUserInfo()
                _userInfoUiState.value = userInfo

                val generalForecast = locationForecastRepository.getGeneralForecast(
                    cords.latitude,
                    cords.longitude,
                    "0",
                    2
                )
                val allAdvice = locationForecastRepository.getAdvice(generalForecast, _userInfoUiState.value)
                _adviceUiState.value = AdviceUiState.Success(allAdvice)

                adviceList = allAdvice


                val graphCoordinates = forecastGraphFunction(locationForecastRepository.getAdviceForecastList(generalForecast))

                val x = graphCoordinates[0]
                val y = graphCoordinates[1]

                Log.i("X:", x.toString())
                Log.i("Y:", y.toString())

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


    //based on temperature, percipitation and UVLimit
    //percipitation mm is (i assume) only based on rain
    //figuring out how to determine snow is not a high enough priority at this stage

    //we are using AdviceForecast because we only need temp, percipitation and UVLimit

    //parameter: a list of (advice) forecast objects that each represent one hour of the day

    fun forecastGraphFunction(forecasts: List<AdviceForecast>): List<List<Int>> {

        var overallRatingList = mutableListOf<Int>()

        var currentHours = mutableListOf<Int>()

        forecasts.forEach {

            val hourOfDay = it.time.toInt()
            currentHours.add(hourOfDay)

            var overallRating: Int
            val tempRating = rating(it.temperature, tempLimitMap)
            val percRating = rating(it.percipitation, percipitationLimitMap)
            val uvRating = rating(it.UVindex, UVLimitMap)

            val ratings = listOf(tempRating, percRating, uvRating)

            overallRating = (tempRating + percRating + uvRating) / 3

            ratings.forEach {
                if (it < 3) {
                    overallRating = it
                }
            }


            overallRatingList.add(overallRating)
        }


        val hourlength = currentHours.size
        val ratinglength = overallRatingList.size

        Log.i("HOURS", "$hourlength")
        Log.i("RATINGS", "$ratinglength")

        Log.i("rating list", overallRatingList.toString())

        return listOf(currentHours, overallRatingList)
    }

    //these maps are used to determine rating
    val tempLimitMap: HashMap<List<Double>, Int> =
        hashMapOf(
            listOf(-20.0, -11.0) to 1,
            listOf(35.1, 45.0) to 1,

            listOf(-10.0, -5.9) to 2,
            listOf(32.1, 35.0) to 2,

            listOf(-5.0, -2.9) to 3,
            listOf(30.1, 32.0) to 3,

            listOf(-2.0, 0.9) to 4,
            listOf(28.1, 30.0) to 4,

            listOf(1.0, 2.9) to 5,
            listOf(26.1, 28.0) to 5,

            listOf(3.0, 5.9) to 6,
            listOf(23.1, 26.0) to 6,

            listOf(6.0, 9.9) to 7,
            listOf(21.1, 23.0) to 7,

            listOf(10.0, 12.9) to 8,
            listOf(19.1, 21.0) to 8,

            listOf(13.0, 14.9) to 9,
            listOf(16.6, 19.0) to 9,

            listOf(15.0, 16.5) to 10
        )

    //basert på info fra Yr
    val percipitationLimitMap: HashMap<List<Double>, Int> =
        hashMapOf(
            listOf(2.1, 7.0) to 1,
            listOf(1.6, 2.0) to 2,
            listOf(1.1, 1.5) to 3,
            listOf(0.9, 1.0) to 4,
            listOf(0.7, 0.8) to 5,
            listOf(0.5, 0.6) to 6,
            listOf(0.3, 0.4) to 7,
            listOf(0.2, 0.2) to 8,
            listOf(0.1, 0.1) to 9,
            listOf(0.0, 0.0) to 10
        )

    //basert på data fra SNL
    val UVLimitMap: HashMap<List<Double>, Int> =
        hashMapOf(
            listOf(8.1, 15.0) to 1,
            listOf(6.6, 8.0) to 2,
            listOf(5.1, 6.5) to 3,
            listOf(4.1, 5.0) to 4,
            listOf(3.7, 4.0) to 5,
            listOf(3.1, 3.6) to 6,
            listOf(2.6, 3.0) to 7,
            listOf(2.1, 2.5) to 8,
            listOf(1.1, 2.0) to 9,
            listOf(0.0, 1.0) to 10
        )


    //this function is used to fetch the rating of a given weather specification
    private fun rating(weatherTypeValue: Double, limitsMap: HashMap<List<Double>, Int>): Int {

        for((key, value) in limitsMap) {
            if (weatherTypeValue in key[0] .. key[1]) {
                return value
            }
        }

        return 0
    }


    fun collectAdviceById(id: Int): Advice{
        return adviceList[id]
    }

}