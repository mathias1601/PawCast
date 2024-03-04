package no.uio.ifi.in2000.team19.prosjekt.model.locationForecast

data class Properties(
    val meta: Meta,
    val timeseries: List<Timeseries>
)