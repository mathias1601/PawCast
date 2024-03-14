package no.uio.ifi.in2000.team19.prosjekt.data

import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.RelevantForecast

interface LocationForecastRepositoryInterface{
    //filterer alt eller det meste her
    suspend fun getAdviceForecastData(latitude: String, longitude: String, height: String): AdviceForecast
    suspend fun getRelevantForecastData(latitude: String, longitude: String, height: String, nrHours: Int) : List<RelevantForecast>
    suspend fun getAdviceNow(latitude: String, longitude: String, height: String):List<Advice> // produserer en liste av Advice basert på LocationForecast for gitte kordinater
}

class LocationForecastRepository(
    val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource()
) :LocationForecastRepositoryInterface {

    override suspend fun getAdviceForecastData(latitude: String, longitude: String, height: String): AdviceForecast {
        val all_data = locationForecastDataSource.getLocationForecast(latitude, longitude, height)

        val temperature = all_data.properties.timeseries[2].data.instant.details.air_temperature.toString()
        val windspeed = all_data.properties.timeseries[2].data.instant.details.wind_speed.toString()

        val adviceForecast = AdviceForecast(temperature, windspeed)

        return adviceForecast
    }

    override suspend fun getRelevantForecastData(latitude: String, longitude: String, height: String, nrHours : Int): List<RelevantForecast> {
        val all_data = locationForecastDataSource.getLocationForecast(latitude, longitude, height)

        val forecastList = mutableListOf<RelevantForecast>()
        for(i in 2..(nrHours+2)) {
            val temperature = all_data.properties.timeseries[i].data.instant.details.air_temperature.toString()
            val windspeed = all_data.properties.timeseries[i].data.instant.details.wind_speed.toString()
            val symbol = all_data.properties.timeseries[i].data.next_1_hours.summary.symbol_code
            val time = all_data.properties.timeseries[i].time
            forecastList.add(RelevantForecast(temperature, windspeed, symbol, time))
        }

        return forecastList
    }

    override suspend fun getAdviceNow(latitude: String, longitude: String, height: String): List<Advice> {
        val adviceList = mutableListOf<Advice>()
        val forecast = getAdviceForecastData(latitude, longitude, height)

        //change conditions when we interview animal and weather expert
        //frozen

        if (forecast.air_temperature.toDouble() < 0) {
            val a = Advice("Frozen", "It's really cold, likely frozen", "Ffff00", forecast) //yellow colour
            adviceList.add(a)
        }
        var a : Advice? = null
        var b = 0
        when (forecast.wind_speed.toDouble()) {
            in 10.0..19.0 -> a = Advice("Gale",  "very windy u choose what u want to do with this info", "Ffff00", forecast)
            in 20.0..24.0 -> a = Advice("Strong Gale", "branches on trees can break, be careful, small animals flyy", "Fed8b1", forecast)
            in 25.0..30.0 -> a = Advice("Storm", "many many wind, dont go out plis?", "Fed8b1", forecast)
            else -> b = 1
        }
        if (b == 0) {
            if (a != null) {
                adviceList.add(a)
            }
        }

        if (adviceList.isEmpty()) {
            adviceList.add(Advice("Safe", "Nothing wrong", "089404", forecast)) // true green color
        }

        return adviceList
    }
}