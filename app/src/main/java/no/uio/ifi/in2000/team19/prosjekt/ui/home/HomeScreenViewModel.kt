package no.uio.ifi.in2000.team19.prosjekt.ui.home

import android.util.Log
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
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.createTemporaryUserinfo
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.ErrorReasons
import java.io.IOException
import java.nio.channels.UnresolvedAddressException
import java.time.LocalDateTime
import javax.inject.Inject


sealed interface AdviceUiState {
    data class Success(val allAdvice: List<Advice>) : AdviceUiState
    data object Loading : AdviceUiState
    data class Error(val errorReason: ErrorReasons) : AdviceUiState
}

data class BestTimesForWalk(
    var morning: String,
    var midday: String,
    var evening: String
)


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val locationForecastRepository: LocationForecastRepository
) : ViewModel() {

    // Contains all advice cards
    private var _adviceUiState: MutableStateFlow<AdviceUiState> =
        MutableStateFlow(AdviceUiState.Loading)
    val adviceUiState: StateFlow<AdviceUiState> = _adviceUiState.asStateFlow()


    private val _graphUiState = MutableStateFlow(CartesianChartModelProducer.build())

    /** Contains graph data */
    val graphUiState: StateFlow<CartesianChartModelProducer> = _graphUiState.asStateFlow()

    private val _bestTimeUiState = MutableStateFlow(BestTimesForWalk("", "", ""))

    /** Contains the best time for a trip for morning, midday and evening based on score. */
    val bestTimeUiState: StateFlow<BestTimesForWalk> = _bestTimeUiState.asStateFlow()

    private val _firstYValueUiState = MutableStateFlow(0)

    /** Contains the value of graphs score on index 0. Used to set graph color to different color based on this value. */
    val firstYValueUiState: StateFlow<Int> = _firstYValueUiState.asStateFlow()

    private var _locationUiState: MutableStateFlow<Cords> =
        MutableStateFlow(Cords(0, "not loaded", "not loaded", "0", "0"))

    /** Contains the users location. Exposed to UI so show location in HomeScreen. */

    val locationUiState: StateFlow<Cords> = _locationUiState.asStateFlow()

    private var _userInfoUiState: MutableStateFlow<UserInfo> =
        MutableStateFlow(createTemporaryUserinfo())

    /** Is exposed to UI to show username and dog name. */
    val userInfoUiState: StateFlow<UserInfo> = _userInfoUiState.asStateFlow()


    private var _temperatureUiState: MutableStateFlow<GeneralForecast> =
        MutableStateFlow(GeneralForecast(0.0, 0.0, "", "", LocalDateTime.now(), 0.0, 0.0, 0.0))

    /** Used to show current temperature. */
    val temperatureUiState: StateFlow<GeneralForecast> = _temperatureUiState.asStateFlow()


    private var _dogImage: MutableStateFlow<Int> = MutableStateFlow(R.drawable.dog_normal)

    /** Is used to determine which to dog show in home screen. */
    val dogImage: StateFlow<Int> = _dogImage.asStateFlow()

    /** height doesnt matter for our use case, so is just always set to 0 */
    private val height: String = "0"

    private lateinit var adviceList: List<Advice>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                settingsRepository.getCords().collect { cords ->
                    _locationUiState.value = cords
                    loadWeatherForecast()
                }
            } catch (e: IOException) {
                _adviceUiState.value = AdviceUiState.Error(ErrorReasons.DATABASE)
            }
        }
    }

    fun loadWeatherForecast() {

        val location = _locationUiState.value
        _adviceUiState.value = AdviceUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {

                Log.d("HomeScreenViewModel", "kaller på getGeneralForecast...")

                val generalForecast = locationForecastRepository.getGeneralForecast(
                    location.latitude,
                    location.longitude,
                    height,
                    2
                )

                _temperatureUiState.value = generalForecast.general[0]

                _userInfoUiState.value = settingsRepository.getUserInfo()

                val allAdvice =
                    locationForecastRepository.getAdvice(generalForecast, _userInfoUiState.value)

                _adviceUiState.value = AdviceUiState.Success(allAdvice)

                adviceList = allAdvice

                _dogImage.value = getWhichDogTypeSymbol(generalForecast.general[0])
                Log.d("Debug", "New dog image: " + _dogImage.value)

                val graphScores = forecastGraphFunction(
                    locationForecastRepository.getAdviceForecastList(generalForecast)
                )


                _firstYValueUiState.value = graphScores[0]

                Log.i("Y:", graphScores.toString())


                _graphUiState.value.tryRunTransaction {
                    lineSeries {
                        series(
                            y = graphScores
                        )
                    }
                }

                // This is how we handle connectivity. Instead of using connectivity manager (which seems like the correct way).
                // Essentially we catch our errors, and use the error message to decode if we lost internet, and update
                // our screen to reflect this. This solution is a LOT simpler, although not as flexible. Landed on this
                // mostly due to time constraints.

                // TODO: Isolate functions of this function into own modules, to better know if internet is the problem of something going wrong....

            } catch (e: IOException) {
                _adviceUiState.value = AdviceUiState.Error(ErrorReasons.INTERRUPTION)
            } catch (e: UnresolvedAddressException) {
                _adviceUiState.value = AdviceUiState.Error(ErrorReasons.INTERNET)
            } catch (e: Exception) {
                _adviceUiState.value = AdviceUiState.Error(ErrorReasons.UNKNOWN)
            }


        }
    }


    //based on temperature, percipitation and UVLimit
    //percipitation mm is (i assume) only based on rain
    //figuring out how to determine snow is not a high enough priority at this stage

    //we are using AdviceForecast because we only need temp, percipitation and UVLimit

    //Parameter: a list of (advice) forecast objects that each represent one hour of the day

    // TODO move to repository or domain layer
    private fun forecastGraphFunction(forecasts: List<AdviceForecast>): MutableList<Int> {

        val overallRatingList = mutableListOf<Int>()
        val currentHours = mutableListOf<String>()
        var bestRatingMorning = 0
        var bestRatingMidday = 0
        var bestRatingEvening = 0

        val bestTimesForWalk = BestTimesForWalk(
            morning = "",
            midday = "",
            evening = ""
        )

        forecasts.forEach { forecast ->

            val hourOfDay = forecast.time
            currentHours.add(hourOfDay)

            val tempRating = rating(forecast.temperature, tempLimitMap)
            val percRating = rating(forecast.precipitation, percipitationLimitMap)
            val uvRating = rating(forecast.UVindex, UVLimitMap)

            val ratings = listOf(tempRating, percRating, uvRating)

            var overallRating: Int = (tempRating + percRating + uvRating) / 3

            ratings.forEach {
                if (it < 3) {
                    overallRating = it
                }
            }


            // Find highest score at morning, midday and evening.
            val hour = hourOfDay.toInt()

            // Morning
            if (hour in 5..10) {
                if (overallRating >= bestRatingMorning && overallRating > 4) {
                    bestRatingMorning = overallRating
                    bestTimesForWalk.morning = hourOfDay
                }
            }

            // Midday
            else if (hour in 10..18) {
                if (overallRating >= bestRatingMidday && overallRating > 4) {
                    bestRatingMidday = overallRating
                    bestTimesForWalk.midday = hourOfDay
                }
            }

            // Evening
            else if (hour in 18..22) {
                if (overallRating >= bestRatingEvening && overallRating > 4) {
                    bestRatingEvening = overallRating
                    bestTimesForWalk.evening = hourOfDay
                }
            }

            overallRatingList.add(overallRating)
        }

        _bestTimeUiState.value = bestTimesForWalk

        // x axis is defined in UI layer based on current time + index of y points.
        return overallRatingList // y axis data
    }

    //these maps are used to determine rating
    private val tempLimitMap: HashMap<List<Double>, Int> =
        hashMapOf(
            listOf(-30.0, -10.1) to 1,
            listOf(30.1, 50.0) to 1,

            listOf(-10.0, -6.0) to 2,
            listOf(29.1, 30.0) to 2,

            listOf(-5.9, -3.0) to 3,
            listOf(28.1, 29.0) to 3,

            listOf(-2.9, 0.9) to 4,
            listOf(25.1, 28.0) to 4,

            listOf(1.0, 2.9) to 5,
            listOf(22.1, 25.0) to 5,

            listOf(3.0, 5.9) to 6,
            listOf(21.1, 22.0) to 6,

            listOf(6.0, 9.9) to 7,
            listOf(19.1, 21.0) to 7,

            listOf(10.0, 12.9) to 8,
            listOf(18.1, 19.0) to 8,

            listOf(13.0, 14.9) to 9,
            listOf(16.6, 18.0) to 9,

            listOf(15.0, 16.5) to 10
        )

    //Basert på info fra Yr
    private val percipitationLimitMap: HashMap<List<Double>, Int> =
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

    //Basert på data fra SNL
    private val UVLimitMap: HashMap<List<Double>, Int> =
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

        for ((key, value) in limitsMap) {
            if (weatherTypeValue in key[0]..key[1]) {
                return value
            }
        }
        return 1
    }

    // used by advice screen. Really simple so didnt move to other viewmodel, as that would require sharing existing advice with that viewmodel.
    fun collectAdviceById(id: Int): Advice {
        return adviceList[id]
    }


    private fun getWhichDogTypeSymbol(weather: GeneralForecast): Int {

        val temperatureToShowSunnyDog = 17.0
        val temperatureToShowColdDog = 0.0

        val isNight = weather.symbol.contains("night", ignoreCase = true)
        val isThundering = weather.symbol.contains("thunder", ignoreCase = true)
        val windSpeed = weather.wind ?: 0.0

        return if (isThundering) R.drawable.dog_thunder
        else if (isNight) R.drawable.dog_sleepy
        else if (windSpeed > 5) R.drawable.dog_wind
        else if (weather.precipitation > 1) R.drawable.dog_rain
        else if (weather.temperature >= temperatureToShowSunnyDog) R.drawable.dog_sunny
        else if (weather.temperature <= temperatureToShowColdDog) R.drawable.dog_cold
        else R.drawable.dog_normal
    }

    fun checkIfUiStateIsError(): Boolean {
        return _adviceUiState.value is AdviceUiState.Error
    }


}