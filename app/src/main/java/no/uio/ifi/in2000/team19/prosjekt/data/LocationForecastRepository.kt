package no.uio.ifi.in2000.team19.prosjekt.data

import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.RelevantForecast

interface LocationForecastRepositoryInterface{
    //filterer alt eller det meste her
    suspend fun getRelevantForecastData(longitude: String, latitude: String, height: String): RelevantForecast
    suspend fun getAdvice(latitude: String, longitude: String):List<Advice> // produserer en liste av Advice basert på LocationForecast for gitte kordinater
}

class LocationForecastRepository(
    val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource()
) :LocationForecastRepositoryInterface {

    override suspend fun getRelevantForecastData(latitude: String, longitude: String, height: String): RelevantForecast {
        val all_data = locationForecastDataSource.getLocationForecast(latitude, longitude, height)

        //TODO: Hent riktig indeks basert på tid
        val temperature = all_data.properties.timeseries[0].data.instant.details.air_temperature.toString()
        val windspeed = all_data.properties.timeseries[0].data.instant.details.wind_speed.toString()

        val relevantForecast = RelevantForecast(temperature, windspeed)

        return relevantForecast
    }

}