package no.uio.ifi.in2000.team19.prosjekt.model.DTO

data class WeatherForDay(
    val lowestTemperature: String,
    val highestTemperature: String,
    val symbol: String,
    val day: String
)
