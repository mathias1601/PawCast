package no.uio.ifi.in2000.team19.prosjekt.model.DTO

import java.time.LocalDate

data class AdviceForecast(
    val temperature: Double,
    val thunderprobability: Double,
    val percipitation: Double,
    val UVindex: Double,
    val date: LocalDate,
    val time: String
)