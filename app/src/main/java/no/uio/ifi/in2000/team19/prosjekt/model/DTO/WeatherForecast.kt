package no.uio.ifi.in2000.team19.prosjekt.model.DTO

data class WeatherForecast(
    val symbol: String,
    val day: String,
    val lowestTemperature: Double? = null,
    val highestTemperature: Double? = null,
    val startingTime: String? = null,
    val endingTime: String? = null,
    val meanTemperature: String? = null,
    val wind: String? = null,
    val precipitation: Double? = null
) 
