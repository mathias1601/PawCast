package no.uio.ifi.in2000.team19.prosjekt.model.DTO

data class AdviceForecast(
    val temperature: Double,
    val thunderprobability: Double,
    val percipitation: Double,
    val UVindex: Double,
    val date: String
)
