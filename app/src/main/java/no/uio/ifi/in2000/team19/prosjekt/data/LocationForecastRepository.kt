package no.uio.ifi.in2000.team19.prosjekt.data

import android.content.Context
import android.util.Log
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.ForecastTypes
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForecast
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

            val date = LocalDateTime.now()

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
    private fun getWeatherForecastForDays(
        locationForecast: LocationForecast,
        nrDays: Int,
        startingHour: Int
    ): List<WeatherForecast> {

        var theHour = startingHour

        var middleOfDay: Int

        val forecastList = mutableListOf<WeatherForecast>()

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
                WeatherForecast(
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


    private fun getWeatherForecastHours(
        locationForecast: LocationForecast,
        startHour: Int
    ): List<WeatherForecast> {

        val forecastList = mutableListOf<WeatherForecast>()

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
                WeatherForecast(
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
    fun getAdvice(generalForecast: ForecastTypes, typeOfDog: UserInfo): List<Advice> {

        val adviceForecast = getAdviceForecastData(generalForecast.general[0])

        val categories = getCategory(adviceForecast, typeOfDog)

        return createAdvice(categories, context)
    }

}


