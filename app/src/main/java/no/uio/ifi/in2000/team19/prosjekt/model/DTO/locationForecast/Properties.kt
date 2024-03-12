package no.uio.ifi.in2000.team19.prosjekt.model.DTO.locationForecast

data class Properties(
    val meta: Meta,
    val timeseries: List<Timeseries>
)