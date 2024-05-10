package no.uio.ifi.in2000.team19.prosjekt.model.DTO

import java.time.LocalDateTime

data class AdviceForecast(
    val temperature: Double,
    val thunderprobability: Double,
    val precipitation: Double,
    val UVindex: Double,
    val date: LocalDateTime,
    val time: String
)