package no.uio.ifi.in2000.team19.prosjekt.model.DTO

data class Advice (
    val title: String, // eks: "Kuldevarsel"
    val description:String, // eks: "Ikke gå tur med hunden uten gode klær, eller finn på aktiviteter inne"
    val color: String, // eks: #FFF000 - viser hvor viktig varsleen, gul = obs, rød = farlig"
    val forecast : RelevantForecast // for å temp, vind, tordenvarsel
)