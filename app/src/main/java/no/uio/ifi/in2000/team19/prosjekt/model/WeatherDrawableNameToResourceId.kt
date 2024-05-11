package no.uio.ifi.in2000.team19.prosjekt.model

import no.uio.ifi.in2000.team19.prosjekt.R

/** Stores a Map that maps all weather file names to their Drawable ID. Done to avoid using the getResource function, which is not recomended.
 *  Creating a map seems like the best approach after reading up on it  */
class WeatherDrawableNameToResourceId {
    companion object {
        val map: Map<String, Int> = mapOf(
            "clearsky_day" to R.drawable.clearsky_day,
            "clearsky_night" to R.drawable.clearsky_night,
            "clearsky_polartwilight" to R.drawable.clearsky_polartwilight,
            "cloudy" to R.drawable.cloudy,
            "fair_day" to R.drawable.fair_day,
            "fair_night" to R.drawable.fair_night,
            "fair_polartwilight" to R.drawable.fair_polartwilight,
            "fog" to R.drawable.fog,
            "heavyrain" to R.drawable.heavyrain,
            "heavyrainandthunder" to R.drawable.heavyrainandthunder,
            "heavyrainshowers_day" to R.drawable.heavyrainshowers_day,
            "heavyrainshowers_night" to R.drawable.heavyrainshowers_night,
            "heavyrainshowers_polartwilight" to R.drawable.heavyrainshowers_polartwilight,
            "heavysleet" to R.drawable.heavysleet,
            "heavysleetandthunder" to R.drawable.heavysleetandthunder,
            "heavysleetshowers_day" to R.drawable.heavysleetshowers_day,
            "heavysleetshowers_night" to R.drawable.heavysleetshowers_night,
            "heavysleetshowers_polartwilight" to R.drawable.heavysleetshowers_polartwilight,
            "heavysnow" to R.drawable.heavysnow,
            "heavysnowandthunder" to R.drawable.heavysnowandthunder,
            "heavysnowshowers_day" to R.drawable.heavysnowshowers_day,
            "heavysnowshowers_night" to R.drawable.heavysnowshowers_night,
            "heavysnowshowers_polartwilight" to R.drawable.heavysnowshowers_polartwilight,
            "lightrain" to R.drawable.lightrain,
            "lightrainandthunder" to R.drawable.lightrainandthunder,
            "lightrainshowers_day" to R.drawable.lightrainshowers_day,
            "lightrainshowers_night" to R.drawable.lightrainshowers_night,
            "lightrainshowers_polartwilight" to R.drawable.lightrainshowers_polartwilight,
            "partlycloudy_day" to R.drawable.partlycloudy_day,
            "partlycloudy_night" to R.drawable.partlycloudy_night,
            "partlycloudy_polartwilight" to R.drawable.partlycloudy_polartwilight,
            "rain" to R.drawable.rain,
            "rainandthunder" to R.drawable.rainandthunder,
            "rainshowers_day" to R.drawable.rainshowers_day,
            "rainshowers_night" to R.drawable.rainshowers_night,
            "rainshowers_polartwilight" to R.drawable.rainshowers_polartwilight,
            "sleet" to R.drawable.sleet,
            "sleetandthunder" to R.drawable.sleetandthunder,
            "sleetshowers_day" to R.drawable.sleetshowers_day,
            "sleetshowers_night" to R.drawable.sleetshowers_night,
            "sleetshowers_polartwilight" to R.drawable.sleetshowers_polartwilight,
            "snow" to R.drawable.snow,
            "snowandthunder" to R.drawable.snowandthunder,
            "snowshowers_day" to R.drawable.snowshowers_day,
            "snowshowers_night" to R.drawable.snowshowers_night,
            "snowshowers_polartwilight" to R.drawable.snowshowers_polartwilight
        )
    }
}