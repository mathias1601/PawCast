package no.uio.ifi.in2000.team19.prosjekt.examples

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team19.prosjekt.model.locationForecast.LocationForecast


fun main() = runBlocking{

    // Coordinates
    val LONGITUDE = "10"
    val LATITUDE = "60"
    val HEIGHT = "0"

    val response = getLocationForecast(LONGITUDE, LATITUDE, HEIGHT) // HttpResponse
    println("Response: ${response.status}")

    val forecast = deserializeLocationForecast(response)

    val temp = getAirTemperatureFromForecast(forecast)

    println("Temperature: $temp degrees")
}


suspend fun getLocationForecast(LONGITUDE:String, LATITUDE:String, HEIGHT:String): HttpResponse {
    val client = HttpClient { install(ContentNegotiation) { gson() }} // set up KTOR client

    val path = "https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT($LONGITUDE+$LATITUDE)&z=$HEIGHT"

    val result = client.get(path)

    client.close()

    return result
}

suspend fun deserializeLocationForecast(response: HttpResponse): LocationForecast {
    return response.body<LocationForecast>()
}

fun getAirTemperatureFromForecast(forecast: LocationForecast): Double {
    return forecast.properties.timeseries[0].data.instant.details.air_temperature
}



