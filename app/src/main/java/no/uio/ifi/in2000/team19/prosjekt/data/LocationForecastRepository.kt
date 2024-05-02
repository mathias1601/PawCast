package no.uio.ifi.in2000.team19.prosjekt.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.patrykandpatrick.vico.core.Animation.range
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.ForecastTypes
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.locationForecast.LocationForecast
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject


class LocationForecastRepository @Inject constructor(
    private val locationForecastDataSource: LocationForecastDataSource,
    private val context: Context
)  {


    //TODO find solution for only one API-call
    private suspend fun fetchLocationForecast(latitude: String, longitude: String, height: String): LocationForecast {
        return locationForecastDataSource.getLocationForecast(latitude, longitude, height)
    }


    fun getAdviceForecastList(listOfGeneralForecasts: ForecastTypes): List<AdviceForecast> {

        val adviceForecasts = mutableListOf<AdviceForecast>()
        val general: List<GeneralForecast> = listOfGeneralForecasts.general
        general.forEach{
            adviceForecasts.add(getAdviceForecastData(it))
        }
        return adviceForecasts
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGeneralForecast(latitude: String, longitude: String, height: String, nrDays: Int): ForecastTypes {

        val locationForecast = fetchLocationForecast(latitude, longitude, height)

        val start = locationForecast.properties.timeseries[0].time
        val dateTime = ZonedDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME)
        val startHour = dateTime.toLocalDateTime().truncatedTo(ChronoUnit.HOURS)

        val now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)

        val hours = ChronoUnit.HOURS.between(startHour, now)
        Log.i("Debugger", "hours aka antall timer mellom starten og nå $hours")

        //If the time is past midnight, hours will get a negative value
        val adjustedStart = if (hours < 0) 24 + hours.toInt() else hours.toInt()
        Log.i("Debugger", "adjusted: $adjustedStart")


        //Find ending point of for-loop.

        val hoursTo23 = (23 - now.hour + 24) % 24 + adjustedStart
        Log.i("Debugger", "hoursTo23 $hoursTo23")
        val startOfNextDay = hoursTo23 + 1
        Log.i("Debugger", "startOfNextDay $startOfNextDay")

        val lastHour = (hoursTo23 + 12)

        val genForecastList = mutableListOf<GeneralForecast>()
        //if (nrHours <= 3)
        for( i in adjustedStart .. lastHour) {
            val temperature = locationForecast.properties.timeseries[i].data.instant.details.air_temperature
            val wind = locationForecast.properties.timeseries[i].data.instant.details.wind_speed
            val symbol = locationForecast.properties.timeseries[i].data.next_1_hours.summary.symbol_code

            val time = locationForecast.properties.timeseries[i].time
            val zonedDateTime = ZonedDateTime.parse(time)
            val hourFormatter = DateTimeFormatter.ofPattern("HH")
            val hourAsInt = zonedDateTime.format(hourFormatter)

            val dateFormatter = DateTimeFormatter.ofPattern("MM-dd")
            val date = zonedDateTime.format(dateFormatter)


            val percipitation = locationForecast.properties.timeseries[i].data.next_1_hours.details.precipitation_amount
            val thunderprobability = locationForecast.properties.timeseries[i].data.next_1_hours.details.probability_of_thunder
            val UVindex = locationForecast.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky

            genForecastList.add(GeneralForecast(temperature, wind, symbol, hourAsInt, date, percipitation, thunderprobability, UVindex, time))

            //startingHour += 1
        }

        val dayForecastList = getWeatherForecastForDays(locationForecast, nrDays, startOfNextDay)
        val meanHours = getWeatherForecastHours(locationForecast, startOfNextDay, 2)

        val forecasts: ForecastTypes = ForecastTypes(genForecastList, dayForecastList, meanHours)

        return forecasts
    }

    //Also possible to do this in the same function. An If-check to see if you want to get for days or hours.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecastForDays(locationForecast: LocationForecast, nrDays: Int, startingHour: Int): List<WeatherForDay> {

        //var dayInTime = 24 - hour
        var theHour = startingHour

        Log.i("Debugger", "theHour $theHour")

        var middleOfDay: Int
        //var coldestTime: Int

        val today = LocalDate.now()


        val forecastList = mutableListOf<WeatherForDay>()

        for (i in 0 until nrDays) {

            val thisDay = today.plusDays(i.toLong() + 1)
            val dayOfWeek = thisDay.dayOfWeek
            val dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale("no", "NO"))

            //Klokka 14
            middleOfDay = theHour + 14

            val temperatures = mutableListOf<Double>()

            for (j in theHour .. theHour + 23) {
                temperatures.add(locationForecast.properties.timeseries[j].data.instant.details.air_temperature)
            }

            val warmestTemperature = temperatures.max()
            val coldestTemperature = temperatures.min()

            //val temperatureWarm = locationForecast.properties.timeseries[warmestTime].data.instant.details.air_temperature
            val nextHoursData = locationForecast.properties.timeseries[middleOfDay].data.next_1_hours
            val symbolCode = nextHoursData?.summary?.symbol_code ?: locationForecast.properties.timeseries[middleOfDay].data.next_6_hours.summary.symbol_code
            //val symbolWarm = locationForecast.properties.timeseries[middleOfDay].data.next_1_hours.summary.symbol_code
            //val temperatureCold = locationForecast.properties.timeseries[coldestTime].data.instant.details.air_temperature

            forecastList.add(WeatherForDay(symbolCode, dayOfWeekString, coldestTemperature, warmestTemperature, ""))

            theHour += 24
        }

        return forecastList
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecastHours(locationForecast: LocationForecast, startHour: Int, nrDays: Int): List<WeatherForDay> {
        //Tar 3 timer om gangen og finner gjennomsnitt og lager et WeatherForDay-object
        val forecastList = mutableListOf<WeatherForDay>()

        var startOfFirstDay = startHour

        val today = LocalDate.now()
        var nextDay = today.plusDays(1)

        //One day is 8, two is 16
        for (i in 0 until 12) {
            val temperatureFirstHour = locationForecast.properties.timeseries[startOfFirstDay].data.instant.details.air_temperature
            val temperatureSecondHour = locationForecast.properties.timeseries[startOfFirstDay + 1].data.instant.details.air_temperature
            val temperatureThirdHour = locationForecast.properties.timeseries[startOfFirstDay + 2].data.instant.details.air_temperature
            val temperatureFourthHour = locationForecast.properties.timeseries[startOfFirstDay + 3].data.instant.details.air_temperature

            val meanTemperature = (temperatureFirstHour + temperatureSecondHour + temperatureThirdHour + temperatureFourthHour) / 4

            val roundedMean = String.format("%.1f", meanTemperature)

            if (i == 6) {
                nextDay = nextDay.plusDays( 1)
            }

            val dayOfWeek = nextDay.dayOfWeek
            val dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale("no", "NO"))

            val nextHoursData = locationForecast.properties.timeseries[startOfFirstDay + 1].data.next_1_hours
            val symbolCode = nextHoursData?.summary?.symbol_code ?: locationForecast.properties.timeseries[startOfFirstDay].data.next_6_hours.summary.symbol_code

            val timeStart = locationForecast.properties.timeseries[startOfFirstDay].time
            val zonedDateTimeStart = ZonedDateTime.parse(timeStart)
            val hourFormat = DateTimeFormatter.ofPattern("HH")
            val startHourAsInt = zonedDateTimeStart.format(hourFormat)

            val timeEnd = locationForecast.properties.timeseries[startOfFirstDay + 3].time
            val zonedDateTimeEnd = ZonedDateTime.parse(timeEnd)
            val hourFormatter = DateTimeFormatter.ofPattern("HH")
            val endHourAsInt = zonedDateTimeEnd.format(hourFormatter)

            forecastList.add(WeatherForDay(symbolCode, dayOfWeekString, null, null, startHourAsInt, endHourAsInt, roundedMean))

            startOfFirstDay += 4
        }

        return forecastList

    }

    //Returnerer en liste av Advice-objekter
    fun getAdvice(generalForecast: ForecastTypes, typeOfDog: UserInfo?): List<Advice> {


        val adviceForecast = when (val firstForecast = generalForecast.general[0]) {
            is GeneralForecast -> getAdviceForecastData(firstForecast)
            else -> return emptyList()  // Eller en annen passende feilhåndtering
        }

        //val dog = getUserInfoDao()
        val categories = getCategory(adviceForecast, typeOfDog)
        return createAdvice(categories)
    }


    //Gjør om fra GeneralForecast til AdviceForecast (fjerner unødvendig dsta)
    private fun getAdviceForecastData(generalForecast: GeneralForecast): AdviceForecast {

        return AdviceForecast(generalForecast.temperature, generalForecast.thunderprobability, generalForecast.percipitation, generalForecast.UVindex, generalForecast.date, generalForecast.hour)
    }


    //Lager AdviceCards, og retunerer en liste av de
    private fun createAdvice(categories: List<AdviceCategory>): List<Advice> {

        val adviceList = mutableListOf<Advice>()

        if (categories[0] == AdviceCategory.SAFE) {
            val safeArray = context.resources.getStringArray(R.array.SAFE)


            val advice = Advice(safeArray[0], safeArray[1], safeArray[2])
            adviceList.add(advice)
            return adviceList
        }

        categories.forEach {category ->

            var adviceArray: Array<String>? = null
            //val resId = context.resources.getIdentifier(category.toString(), "array", context.packageName)
            //val adviceArray: Array<String> = context.resources.getStringArray(resId)
            when (category.toString()) {
                "COOL" -> adviceArray = context.resources.getStringArray(R.array.COOL)
                "COOLOTHER" -> adviceArray = context.resources.getStringArray(R.array.COOLOTHER)
                "COLD" -> adviceArray = context.resources.getStringArray(R.array.COLD)
                "COLDLONGFUR" -> adviceArray = context.resources.getStringArray(R.array.COLDLONGFUR)
                "COLDOTHER" -> adviceArray = context.resources.getStringArray(R.array.COLDOTHER)
                "FREEZING" -> adviceArray = context.resources.getStringArray(R.array.FREEZING)
                "SALT" -> adviceArray = context.resources.getStringArray(R.array.SALT)
                "WARM" -> adviceArray = context.resources.getStringArray(R.array.WARM)
                "WARMFLAT" -> adviceArray = context.resources.getStringArray(R.array.WARMFLAT)
                "VERYWARM" -> adviceArray = context.resources.getStringArray(R.array.VERYWARM)
                "VERYWARMFLAT" -> adviceArray = context.resources.getStringArray(R.array.VERYWARMFLAT)
                "HEATWAVE" -> adviceArray = context.resources.getStringArray(R.array.HEATWAVE)
                "RAIN" -> adviceArray = context.resources.getStringArray(R.array.RAIN)
                "THUNDER" -> adviceArray = context.resources.getStringArray(R.array.THUNDER)
                "SNOW" -> adviceArray = context.resources.getStringArray(R.array.SNOW)
                "SUNBURN" -> adviceArray = context.resources.getStringArray(R.array.SUNBURN)
                "TICK" -> adviceArray = context.resources.getStringArray(R.array.TICK)
                "VIPER" -> adviceArray = context.resources.getStringArray(R.array.VIPER)
                "CAR" -> adviceArray = context.resources.getStringArray(R.array.CAR)


            }

            var counter = 0
            if (adviceArray != null) {
                while  (counter < adviceArray.size){

                    val title = adviceArray.get(counter).toString()
                    val description = adviceArray?.get(counter+1).toString()
                    val shortAdvice = adviceArray?.get(counter+2).toString()

                    val advice = Advice(title, description, shortAdvice)
                    adviceList.add(advice)

                    counter+=3

                }
            }
        }

        return adviceList
    }

    private fun getCategory(adviceForecast: AdviceForecast, typeOfDog: UserInfo?): List<AdviceCategory> {

        //TODO userinfo er nullable???
        //if (typeOfDog != null)
        var categoryList = mutableListOf<AdviceCategory>()

        if (typeOfDog == null) {
            categoryList.add(AdviceCategory.SAFE)
            return categoryList
        }

        val weatherLimitsMap = mapOf(
            AdviceCategory.COOL to listOf(-5.0, 0.0),
            AdviceCategory.COLD to listOf(-15.0, -5.0),
            AdviceCategory.FREEZING to listOf(-70.0, -15.0),
            AdviceCategory.SALT to listOf(-8.0, 4.0),
            AdviceCategory.WARM to listOf(15.0, 23.0),
            AdviceCategory.VERYWARM to listOf(23.0, 30.0),
            AdviceCategory.HEATWAVE to listOf(30.0, 70.0),
            AdviceCategory.CAR to listOf(18.0, 70.0)
        )

        weatherLimitsMap.forEach { (category, limits) ->
            if (adviceForecast.temperature in limits[0] .. limits[1]) {
                categoryList.add(category)
            }
        }

        if (typeOfDog!!.isThin ||
            typeOfDog.isPuppy ||
            typeOfDog.isShortHaired ||
            typeOfDog.isSenior ||
            typeOfDog.isThinHaired) {

            if (AdviceCategory.COOL in categoryList) {
                categoryList.add(AdviceCategory.COOLOTHER)
                Log.i("KATEGORIER", "Legger til coolother")
            }

            if (AdviceCategory.COLD in categoryList) {
                categoryList.add(AdviceCategory.COLDOTHER)
                Log.i("KATEGORIER", "Legger til coldother")
            }
        }


        if (typeOfDog.isFlatNosed) {

            if (AdviceCategory.WARM in categoryList) {
                categoryList.add(AdviceCategory.WARMFLAT)
                Log.i("KATEGORIER", "Legger til warmflat")
            }

            if (AdviceCategory.VERYWARM in categoryList) {
                categoryList.add(AdviceCategory.VERYWARMFLAT)
                Log.i("KATEGORIER", "Legger til verywarmflat")
            }
        }

        if (typeOfDog.isLongHaired && AdviceCategory.COLD in categoryList) {
            categoryList.add(AdviceCategory.COLDLONGFUR)
        }


        if (adviceForecast.UVindex >= 3 && (
                    typeOfDog.isThinHaired ||
                            typeOfDog.isLightHaired ||
                            typeOfDog.isShortHaired)) {
            categoryList.add(AdviceCategory.SUNBURN)
            Log.i("KATEGORIER", "Legger til sunburn")
        }

        //TODO find right number
        if (adviceForecast.thunderprobability >= 50) {
            categoryList.add(AdviceCategory.THUNDER)
            Log.i("KATEGORIER", "Legger til thunder")
        }

        //TODO find right number
        if (adviceForecast.percipitation >= 0.1) {
            categoryList.add(AdviceCategory.RAIN)
        }

        //Finne datoen i dag og sjekke om den er innenfor en range hvor flått og hoggorm er aktive


        if (categoryList.size == 0) {
            categoryList.add(AdviceCategory.SAFE)
        }

        return categoryList
    }
}

