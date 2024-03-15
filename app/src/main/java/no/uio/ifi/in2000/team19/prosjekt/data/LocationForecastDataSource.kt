package no.uio.ifi.in2000.team19.prosjekt.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.locationForecast.LocationForecast

class LocationForecastDataSource {

    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun getLocationForecast (LATITUDE: String, LONGITUDE: String, HEIGHT: String): LocationForecast {
        //TODO: Swap complete to compact later maybe
        val path = "https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT($LONGITUDE+$LATITUDE)&z=$HEIGHT"

        val result = client.get(path)

        val forecast = result.body<LocationForecast>()

        client.close()

        return forecast
    }


}