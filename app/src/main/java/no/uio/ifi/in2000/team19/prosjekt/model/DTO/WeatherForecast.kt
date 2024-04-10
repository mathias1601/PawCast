package no.uio.ifi.in2000.team19.prosjekt.model.DTO

data class WeatherForecast(
    val temperature:String,
    val wind: String,
    val symbol : String,
    val time: String,
    val downpour: String
)

//Kun data relatert til WeatherForecast; tid, temperatur, wind og symbolkode
