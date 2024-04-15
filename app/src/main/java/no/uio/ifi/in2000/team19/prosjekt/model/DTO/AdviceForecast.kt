package no.uio.ifi.in2000.team19.prosjekt.model.DTO

data class AdviceForecast(
    val temperature:Float,
    val thunderprobability: Float,
    val downpour: Float,
    val UVindex: Float,
    val time: String
)
