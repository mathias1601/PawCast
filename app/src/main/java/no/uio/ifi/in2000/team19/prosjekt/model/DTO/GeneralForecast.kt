package no.uio.ifi.in2000.team19.prosjekt.model.DTO

data class GeneralForecast(
    val temperature:Float,
    val wind: Float,
    val symbol : String,
    val time: String,
    val downpour: Float,
    val thunderprobability: Float,
    val UVindex: Float
)

//TODO add all relevant data


//VI HAR DENNE FORDI:::
// henter ut relevante ting fra locationforecast som har veldig mye greier
// da er det enklere å sortere inn i advice og weather etterpå
// :) godta det pls takk