package no.uio.ifi.in2000.team19.prosjekt.model.DTO

data class GeneralForecast (
    val temperature:Double,
    val wind: Double? = null,
    val symbol : String,
    val hour: String,
    val date: String,
    val percipitation: Double,
    val thunderprobability: Double,
    val UVindex: Double,
    val time: String
)

//TODO add all relevant data


//VI HAR DENNE FORDI:::
// henter ut relevante ting fra locationforecast som har veldig mye greier
// da er det enklere å sortere inn i advice og weather etterpå
// :) godta det pls takk