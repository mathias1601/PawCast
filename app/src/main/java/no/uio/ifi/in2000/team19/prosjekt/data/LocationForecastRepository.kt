package no.uio.ifi.in2000.team19.prosjekt.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.ForecastTypes
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.locationForecast.LocationForecast
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject


class LocationForecastRepository @Inject constructor(
    private val locationForecastDataSource: LocationForecastDataSource,
    private val context: Context
) {


    //TODO find solution for only one API-call
    private suspend fun fetchLocationForecast(
        latitude: String,
        longitude: String,
        height: String
    ): LocationForecast {
        return locationForecastDataSource.getLocationForecast(latitude, longitude, height)
    }


    fun getAdviceForecastList(listOfGeneralForecasts: ForecastTypes): List<AdviceForecast> {

        val adviceForecasts = mutableListOf<AdviceForecast>()
        val general: List<GeneralForecast> = listOfGeneralForecasts.general
        general.forEach {
            adviceForecasts.add(getAdviceForecastData(it))
        }
        return adviceForecasts
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGeneralForecast(
        latitude: String,
        longitude: String,
        height: String,
        nrDays: Int
    ): ForecastTypes {

        Log.d("DEBUG", "getGeneralForecast kalt på...")

        val locationForecast = fetchLocationForecast(latitude, longitude, height)

        val start = locationForecast.properties.timeseries[0].time
        val dateTime = ZonedDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME)
        val startHour = dateTime.toLocalDateTime().truncatedTo(ChronoUnit.HOURS)

        val now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)

        val hours = ChronoUnit.HOURS.between(startHour, now)

        //If the time is past midnight, hours will get a negative value
        val adjustedStart = if (hours < 0) 24 + hours.toInt() else hours.toInt()

        //Find ending point of for-loop.
        val hoursTo23 = (23 - now.hour + 24) % 24 + adjustedStart

        //Used for the other weather functions
        val startOfNextDay = hoursTo23 + 1

        val lastHour = (hoursTo23 + 12)

        val genForecastList = mutableListOf<GeneralForecast>()

        for (i in adjustedStart..lastHour) {
            val temperature =
                locationForecast.properties.timeseries[i].data.instant.details.air_temperature
            val wind = locationForecast.properties.timeseries[i].data.instant.details.wind_speed
            val symbol =
                locationForecast.properties.timeseries[i].data.next_1_hours.summary.symbol_code

            val time = locationForecast.properties.timeseries[i].time
            val zonedDateTime = ZonedDateTime.parse(time)
            val hourFormatter = DateTimeFormatter.ofPattern("HH")
            val hourAsInt = zonedDateTime.format(hourFormatter).toString()

            val date = zonedDateTime.toLocalDate()

            val precipitation =
                locationForecast.properties.timeseries[i].data.next_1_hours.details.precipitation_amount
            val thunderProbability =
                locationForecast.properties.timeseries[i].data.next_1_hours.details.probability_of_thunder
            val uvIndex =
                locationForecast.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky

            genForecastList.add(
                GeneralForecast(
                    temperature,
                    wind,
                    symbol,
                    hourAsInt,
                    date,
                    precipitation,
                    thunderProbability,
                    uvIndex,
                )
            )

            //startingHour += 1
        }

        val dayForecastList = getWeatherForecastForDays(locationForecast, nrDays, startOfNextDay)
        val meanHours = getWeatherForecastHours(locationForecast, startOfNextDay)

        return ForecastTypes(genForecastList, dayForecastList, meanHours)
    }

    //Also possible to do this in the same function. An If-check to see if you want to get for days or hours.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecastForDays(
        locationForecast: LocationForecast,
        nrDays: Int,
        startingHour: Int
    ): List<WeatherForDay> {

        var theHour = startingHour

        var middleOfDay: Int

        val forecastList = mutableListOf<WeatherForDay>()

        for (i in 0 until nrDays) {

            val thisDay = LocalDate.now().plusDays(i.toLong() + 1)
            val dayOfWeekString =
                thisDay.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("no", "NO"))

            //12:00
            middleOfDay = theHour + 12

            val temperatures = mutableListOf<Double>()

            var hourCounter = theHour

            repeat(4) {
                temperatures.add(locationForecast.properties.timeseries[hourCounter].data.next_6_hours.details.air_temperature_max)
                temperatures.add(locationForecast.properties.timeseries[hourCounter].data.next_6_hours.details.air_temperature_min)
                hourCounter += 6
            }

            val warmestTemperature = temperatures.max()
            val coldestTemperature = temperatures.min()

            val symbolCode =
                locationForecast.properties.timeseries[middleOfDay].data.next_6_hours.summary.symbol_code

            forecastList.add(
                WeatherForDay(
                    symbolCode,
                    dayOfWeekString,
                    coldestTemperature,
                    warmestTemperature
                )
            )

            theHour += 24
        }

        return forecastList
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecastHours(
        locationForecast: LocationForecast,
        startHour: Int
    ): List<WeatherForDay> {

        val forecastList = mutableListOf<WeatherForDay>()

        var theHour = startHour

        var nextDay = LocalDate.now().plusDays(1)

        //One day is 8, two is 16
        repeat(8) { i ->

            var firstHour: String = locationForecast.properties.timeseries[theHour].time
            var secondHour: String = locationForecast.properties.timeseries[theHour + 1].time
            var lastHour: String = locationForecast.properties.timeseries[theHour + 6].time

            val endHour: String
            var meanWind = 0.0

            //Only want the hour
            firstHour = firstHour.substring(11, 13)
            secondHour = secondHour.substring(11, 13)
            lastHour = lastHour.substring(11, 13)

            val firstHourAsInt = firstHour.toInt()
            val secondHourInt = secondHour.toInt()

            val hoursBetween = if (firstHourAsInt <= secondHourInt) {
                secondHourInt - firstHourAsInt
            } else {
                (24 - firstHourAsInt + secondHourInt)
            }

            //Sometimes the difference between two timeseries is 6 hours instead of one
            if (hoursBetween == 6) {

                meanWind =
                    (locationForecast.properties.timeseries[theHour].data.instant.details.wind_speed +
                            locationForecast.properties.timeseries[theHour + 1].data.instant.details.wind_speed) / 2

                endHour = secondHour
            } else {

                var hourCounter = theHour

                repeat(6) {
                    meanWind += locationForecast.properties.timeseries[hourCounter].data.instant.details.wind_speed
                    hourCounter += 1
                }


                endHour = lastHour
            }

            val meanTemperature =
                (locationForecast.properties.timeseries[theHour].data.next_6_hours.details.air_temperature_max +
                        locationForecast.properties.timeseries[theHour].data.next_6_hours.details.air_temperature_min) / 2

            val symbolCode =
                locationForecast.properties.timeseries[theHour].data.next_6_hours.summary.symbol_code
            val precipitation =
                locationForecast.properties.timeseries[theHour].data.next_6_hours.details.precipitation_amount

            val roundedTemperature = String.format("%.1f", meanTemperature)
            val roundedWind = String.format("%.1f", meanWind)

            if (i == 4) {
                nextDay = nextDay.plusDays(1)
            }

            val dayOfWeekString =
                nextDay.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("no", "NO"))

            forecastList.add(
                WeatherForDay(
                    symbolCode,
                    dayOfWeekString,
                    null,
                    null,
                    firstHour,
                    endHour,
                    roundedTemperature,
                    roundedWind,
                    precipitation
                )
            )

            theHour += 6
        }

        return forecastList

    }

    //Returnerer en liste av Advice-objekter
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAdvice(generalForecast: ForecastTypes, typeOfDog: UserInfo): List<Advice> {

        val adviceForecast = getAdviceForecastData(generalForecast.general[0])

        val categories = getCategory(adviceForecast, typeOfDog)

        return createAdvice(categories)
    }


    //Gjør om fra GeneralForecast til AdviceForecast (fjerner unødvendig dsta)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAdviceForecastData(generalForecast: GeneralForecast): AdviceForecast {

        return AdviceForecast(
            generalForecast.temperature,
            generalForecast.thunderprobability,
            generalForecast.percipitation,
            generalForecast.UVindex,
            generalForecast.date,
            generalForecast.hour
        )
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

        categories.forEach { category ->

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
                "VERYWARMFLAT" -> adviceArray =
                    context.resources.getStringArray(R.array.VERYWARMFLAT)

                "HEATWAVE" -> adviceArray = context.resources.getStringArray(R.array.HEATWAVE)
                "RAIN" -> adviceArray = context.resources.getStringArray(R.array.RAIN)
                "THUNDER" -> adviceArray = context.resources.getStringArray(R.array.THUNDER)
                "SUNBURN" -> adviceArray = context.resources.getStringArray(R.array.SUNBURN)
                "TICK" -> adviceArray = context.resources.getStringArray(R.array.TICK)
                "VIPER" -> adviceArray = context.resources.getStringArray(R.array.VIPER)
                "CAR" -> adviceArray = context.resources.getStringArray(R.array.CAR)


            }

            var counter = 0
            if (adviceArray != null) {
                while (counter < adviceArray.size) {

                    val title = adviceArray[counter]
                    val description = adviceArray[counter + 1]
                    val shortAdvice = adviceArray[counter + 2]

                    val advice = Advice(title, description, shortAdvice)
                    adviceList.add(advice)

                    counter += 3

                }
            }
        }

        return adviceList
    }

}


