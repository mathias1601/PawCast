package no.uio.ifi.in2000.team19.prosjekt.model.locationForecast

data class LocationForecast(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)