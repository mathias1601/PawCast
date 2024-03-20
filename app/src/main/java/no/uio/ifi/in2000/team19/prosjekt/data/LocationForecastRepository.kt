package no.uio.ifi.in2000.team19.prosjekt.data

import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast


class LocationForecastRepository(
    private val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource()
)  {


    suspend fun getGeneralForecast(latitude: String, longitude: String, height: String, nrHours: Int): List<GeneralForecast> {

        val locationForecast = locationForecastDataSource.getLocationForecast(latitude, longitude, height)

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


    fun getAdvice(generalForecast:List<GeneralForecast>): List<Advice> {
        val adviceForecast = getAdviceForecastData(generalForecast[0]) // only get forecast for right now.
        // val fakeAdviceForTestingForecast = AdviceForecast(temperature = "-10.0", windspeed = "10")
        return createAdvice(adviceForecast)
    }


    private fun getAdviceForecastData(generalForecast: GeneralForecast): AdviceForecast {
        return AdviceForecast(generalForecast.temperature, generalForecast.wind)
    }


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