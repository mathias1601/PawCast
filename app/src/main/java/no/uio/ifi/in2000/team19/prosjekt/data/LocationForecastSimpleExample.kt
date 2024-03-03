package no.uio.ifi.in2000.team19.prosjekt.data

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking


fun main(){
    val client = HttpClient() // set up KTOR client

    // IFI coordinates
    val LATITUDE = 60
    val LONGITUDE = 10
    val HEIGHT = 0

    val path = "https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT($LONGITUDE+$LATITUDE)&z=$HEIGHT"

    runBlocking {
        val result = client.get(path)
        println("Status: ${result.status}")
    }
}