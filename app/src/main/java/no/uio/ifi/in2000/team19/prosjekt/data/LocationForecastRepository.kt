package no.uio.ifi.in2000.team19.prosjekt.data

import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.locationForecast.LocationForecast
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


class LocationForecastRepository(
    private val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource()
)  {

    private var lastLocationForecast: LocationForecast? = null

    //TODO find solution for only one API-call
    private suspend fun fetchLocationForecast(latitude: String, longitude: String, height: String): LocationForecast {
        lastLocationForecast = locationForecastDataSource.getLocationForecast(latitude, longitude, height)
        return lastLocationForecast as LocationForecast
    }
    suspend fun getGeneralForecast(latitude: String, longitude: String, height: String, nrHours: Int): List<GeneralForecast> {

        val locationForecast = fetchLocationForecast(latitude, longitude, height)

        val forecastList = mutableListOf<GeneralForecast>()

        for( i in 2..(nrHours+2) ) {
            val temperature = locationForecast.properties.timeseries[i].data.instant.details.air_temperature.toString()
            val wind = locationForecast.properties.timeseries[i].data.instant.details.wind_speed.toString()
            val symbol = locationForecast.properties.timeseries[i].data.next_1_hours.summary.symbol_code
            val time = locationForecast.properties.timeseries[i].time
            forecastList.add(GeneralForecast(temperature, wind, symbol, time))
        }

        return forecastList
    }

    //Also possible to do this in the same function. An If-check to see if you want to get for days or hours.
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGeneralForecastForDays(latitude: String, longitude: String, height: String, nrDays: Int): List<WeatherForDay> {

        val locationForecast = fetchLocationForecast(latitude, longitude, height)

        val updatedAt = locationForecast.properties.meta.updated_at
        val dateTime = ZonedDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME)
        val hour = dateTime.toLocalDateTime().withMinute(0).withSecond(0).withNano(0).hour

        var dayInTime = 24 - hour

        var warmestTime: Int
        var coldestTime: Int

        val today = LocalDate.now()


        val forecastList = mutableListOf<WeatherForDay>()

        for(i in 0 until nrDays) {

            val thisDay = today.plusDays(i.toLong() + 1)
            val dayOfWeek = thisDay.dayOfWeek
            val dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

            //Klokka 14
            warmestTime = dayInTime + 14

            //Klokka 02
            coldestTime = dayInTime + 2

            val temperatureWarm = locationForecast.properties.timeseries[warmestTime].data.instant.details.air_temperature.toString()
            val symbolWarm = locationForecast.properties.timeseries[warmestTime].data.next_1_hours.summary.symbol_code
            val temperatureCold = locationForecast.properties.timeseries[coldestTime].data.instant.details.air_temperature.toString()

            forecastList.add(WeatherForDay(temperatureCold, temperatureWarm, symbolWarm, dayOfWeekString))

            dayInTime += 24
        }

        return forecastList
    }

    //Returnerer en liste av Advice-objekter
    fun getAdvice(generalForecast:List<GeneralForecast>): List<Advice> {
        val adviceForecast = getAdviceForecastData(generalForecast[0]) // only get forecast for right now.
        // val fakeAdviceForTestingForecast = AdviceForecast(temperature = "-10.0", windspeed = "10")
        return createAdvice(adviceForecast)
    }


    //Gjør om fra GeneralForecast til AdviceForecast (fjerner unødvendig dsta)
    private fun getAdviceForecastData(generalForecast: GeneralForecast): AdviceForecast {
        return AdviceForecast(generalForecast.temperature, generalForecast.wind)
    }


    //Lager AdviceCards, og retunerer en liste av de
    private fun createAdvice(forecast: AdviceForecast): List<Advice> {

        val adviceList = mutableListOf<Advice>()

        if (forecast.temperature.toDouble() < 0) {
            val advice = Advice("Frozen", "It's really cold, likely frozen", "#FFFF00", forecast) //yellow colour
            adviceList.add(advice)
        }

        var advice : Advice? = null
        var adviceCount = 0

        when (forecast.windspeed.toDouble()) {
            in 10.0..19.0 -> advice = Advice("Gale",  "very windy u choose what u want to do with this info", "#FFFF00", forecast)
            in 20.0..24.0 -> advice = Advice("Strong Gale", "branches on trees can break, be careful, small animals flyy", "#FED8B1", forecast)
            in 25.0..30.0 -> advice = Advice("Storm", "many many wind, dont go out plis?", "#FED8B1", forecast)
            else -> adviceCount = 1
        }

        if (adviceCount == 0) {
            if (advice != null) {
                adviceList.add(advice)
            }
        }

        if (adviceList.isEmpty()) {
            adviceList.add(Advice("Safe", "Nothing wrong", "#008000", forecast)) // true green color
        }

        return adviceList
    }
}