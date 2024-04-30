package no.uio.ifi.in2000.team19.prosjekt.model.DTO

import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay

data class ForecastTypes(
    val general: List<GeneralForecast>,
    val day: List<WeatherForDay>,
    val hours: List<WeatherForDay>
)
