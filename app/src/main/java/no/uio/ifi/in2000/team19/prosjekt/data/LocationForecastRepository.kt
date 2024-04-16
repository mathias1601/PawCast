package no.uio.ifi.in2000.team19.prosjekt.data

import android.content.Context
import android.icu.lang.UCharacter.DecompositionType.SMALL
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.locationForecast.LocationForecast
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject


class LocationForecastRepository @Inject constructor(
    private val locationForecastDataSource: LocationForecastDataSource,
    @ApplicationContext private val context: Context
)  {

    private var lastLocationForecast: LocationForecast? = null

    //TODO find solution for only one API-call
    private suspend fun fetchLocationForecast(latitude: String, longitude: String, height: String): LocationForecast {
        lastLocationForecast = locationForecastDataSource.getLocationForecast(latitude, longitude, height)
        return lastLocationForecast as LocationForecast
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGeneralForecast(latitude: String, longitude: String, height: String, nrHours: Int): List<WeatherForecast> {

        val locationForecast = fetchLocationForecast(latitude, longitude, height)

        val updatedAt = locationForecast.properties.meta.updated_at
        val dateTime = ZonedDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME)
        val hour = dateTime.toLocalDateTime().withMinute(0).withSecond(0).withNano(0).hour

        val now = LocalDateTime.now()
        val hourRounded = now.truncatedTo(ChronoUnit.HOURS).hour

        var startingHour = hour - hourRounded

        val forecastList = mutableListOf<WeatherForecast>()

        for( i in 0 until nrHours) {
            val temperature = locationForecast.properties.timeseries[startingHour].data.instant.details.air_temperature.toString()
            val wind = locationForecast.properties.timeseries[startingHour].data.instant.details.wind_speed.toString()
            val symbol = locationForecast.properties.timeseries[startingHour].data.next_1_hours.summary.symbol_code
            val time = locationForecast.properties.timeseries[startingHour].time
            val percipitation = locationForecast.properties.timeseries[startingHour].data.next_1_hours.details.precipitation_amount.toString()
            forecastList.add(WeatherForecast(temperature, wind, symbol, time, percipitation))

            startingHour += 1
        }

        return forecastList
    }

    //Also possible to do this in the same function. An If-check to see if you want to get for days or hours.
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGeneralForecastForDays(latitude: String, longitude: String, height: String, nrDays: Int): List<WeatherForDay> {

        val locationForecast = fetchLocationForecast(latitude, longitude, height)

        val updatedAt = locationForecast.properties.meta.updated_at
        val dateTime = ZonedDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME)
        val hour = dateTime.toLocalDateTime().withMinute(0).withSecond(0).withNano(0).hour

        var dayInTime = 24 - hour

        var warmestTime: Int
        var coldestTime: Int

        val today = LocalDate.now()


        val forecastList = mutableListOf<WeatherForDay>()

        for (i in 0 until nrDays) {

            val thisDay = today.plusDays(i.toLong() + 1)
            val dayOfWeek = thisDay.dayOfWeek
            val dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

            //Klokka 14
            warmestTime = dayInTime + 14

            //Klokka 02
            coldestTime = dayInTime + 2

            val temperatureWarm = locationForecast.properties.timeseries[warmestTime].data.instant.details.air_temperature.toString()
            val symbolWarm = locationForecast.properties.timeseries[warmestTime].data.next_1_hours.summary.symbol_code
            val temperatureCold = locationForecast.properties.timeseries[coldestTime].data.instant.details.air_temperature.toString()

            forecastList.add(WeatherForDay(temperatureCold, temperatureWarm, symbolWarm, dayOfWeekString, ""))

            dayInTime += 24
        }

        return forecastList
    }

    //Returnerer en liste av Advice-objekter
    fun getAdvice(generalForecast:List<GeneralForecast>): List<Advice> {
        val adviceForecast = getAdviceForecastData(generalForecast[0]) // only get forecast for right now.
        // val fakeAdviceForTestingForecast = AdviceForecast(temperature = "-10.0", windspeed = "10")
        return createAdvice(adviceForecast)
    }


    //Gjør om fra GeneralForecast til AdviceForecast (fjerner unødvendig dsta)
    private fun getAdviceForecastData(generalForecast: GeneralForecast): AdviceForecast {
        return AdviceForecast(generalForecast.temperature, generalForecast.wind)
    }


    //Lager AdviceCards, og retunerer en liste av de
    private fun createAdvice(categories: List<AdviceCategory>): List<Advice> {

        val adviceList = mutableListOf<Advice>()

        if (categories[0].equals("SAFE")) {
            val title = "Trygt"
            val description = "Ingen varsler"
            val shortAdvice = "Ingen varsler"

            val advice = Advice(title, description, shortAdvice)
            adviceList.add(advice)
            return adviceList
        }

        categories.forEach {category ->

            var adviceArray: Array<String>? = null
            //val resId = context.resources.getIdentifier(category.toString(), "array", context.packageName)
            //val adviceArray: Array<String> = context.resources.getStringArray(resId)
            when (category.toString()) {
                "COLD" -> adviceArray = context.resources.getStringArray(R.array.COLD)
                "COLDSMALL" -> adviceArray = context.resources.getStringArray(R.array.COLDSMALL)
                "COLDBIG" -> adviceArray = context.resources.getStringArray(R.array.COLDBIG)
                //"COLDFLAT" -> adviceArray = context.resources.getStringArray(R.array.COLDFLAT)
                "VERYCOLD" -> adviceArray = context.resources.getStringArray(R.array.VERYCOLD)
                "FREEZING" -> adviceArray = context.resources.getStringArray(R.array.FREEZING)
                "SALT" -> adviceArray = context.resources.getStringArray(R.array.SALT)
                "WARM" -> adviceArray = context.resources.getStringArray(R.array.WARM)
                "WARMFLAT" -> adviceArray = context.resources.getStringArray(R.array.WARMFLAT)
                "VERYWARM" -> adviceArray = context.resources.getStringArray(R.array.VERYWARM)
                "HEATWAVE" -> adviceArray = context.resources.getStringArray(R.array.HEATWAVE)
                "RAIN" -> adviceArray = context.resources.getStringArray(R.array.RAIN)
                "THUNDER" -> adviceArray = context.resources.getStringArray(R.array.THUNDER)
                "SNOW" -> adviceArray = context.resources.getStringArray(R.array.SNOW)
                "SUNBURN" -> adviceArray = context.resources.getStringArray(R.array.SUNBURN)
                "TICK" -> adviceArray = context.resources.getStringArray(R.array.TICK)
                "VIPER" -> adviceArray = context.resources.getStringArray(R.array.VIPER)


            }

            var counter = 0
            while  (counter < adviceArray!!.size){

                val title = adviceArray.get(counter).toString()
                val description = adviceArray?.get(counter+1).toString()
                val shortAdvice = adviceArray?.get(counter+2).toString()

                val advice = Advice(title, description, shortAdvice)
                adviceList.add(advice)

                counter+=3

            }




        }


        return adviceList
    }

    private fun getCategory(adviceForecast: AdviceForecast, typeOfDog: String): List<AdviceCategory> {

        var categoryList = mutableListOf<AdviceCategory>()

        val weatherLimitsMap = mapOf(
            AdviceCategory.COLD to listOf(-5.0, 0.0),
            AdviceCategory.VERYCOLD to listOf(-15.0, -5.0),
            AdviceCategory.FREEZING to listOf(-70.0, -15.0),
            AdviceCategory.SALT to listOf(-8.0, 4.0),
            AdviceCategory.WARM to listOf(15.0, 23.0),
            AdviceCategory.VERYWARM to listOf(23.0, 30.0),
            AdviceCategory.HEATWAVE to listOf(30.0, 70.0),
            AdviceCategory.CAR to listOf(18.0, 70.0)
        )

        weatherLimitsMap.forEach { (category, limits) ->
            if (adviceForecast.temperature in limits[0] .. limits[1]) {
                categoryList.add(category)
            }
        }

        if (AdviceCategory.WARM in categoryList && typeOfDog == "FLAT") {
            categoryList.add(AdviceCategory.WARMFLAT)
        }

        if (AdviceCategory.COLD in categoryList && typeOfDog == "SMALL") {
            categoryList.add(AdviceCategory.COLDSMALL)
        }

        if (adviceForecast.UVindex >= 2.5) {
            categoryList.add(AdviceCategory.SUNBURN)
        }

        //TODO find right number
        if (adviceForecast.thunderprobability >= 50) {
            categoryList.add(AdviceCategory.THUNDER)
        }

        //TODO find right number
        if (adviceForecast.downpour >= 50) {
            categoryList.add(AdviceCategory.RAIN)
        }

        //Finne datoen i dag og sjekke om den er innenfor en range hvor flått og hoggorm er aktive



        /*
        AdviceCategory.SAFE to listOf()

        AdviceCategory.SNOW to listOf()

        Hvordan vite om det snør?????

        AdviceCategory.SUNBURN to listOf()
        AdviceCategory.THUNDER to listOf()
        AdviceCategory.TICK to listOf()
        AdviceCategory.VIPER to listOf()
        AdviceCategory.RAIN to listOf(),

         */
        /*if (adviceForecast.temperature > -5 && adviceForecast.temperature <= 0) {
            categoryList.add(AdviceCategory.COLD)
        }

        if (adviceForecast.temperature > -15 && adviceForecast.temperature <= -5) {
            categoryList.add(AdviceCategory.VERYCOLD)
        }

        if (adviceForecast.temperature <= -15) {
            categoryList.add(AdviceCategory.FREEZING)
        }

        if (adviceForecast.temperature >= -8 && adviceForecast.temperature <= +4) {
            categoryList.add(AdviceCategory.SALT)
        }

        if (adviceForecast.temperature in 15.0..23.0) {
            categoryList.add(AdviceCategory.WARM)

            if (typeOfDog == FLAT) {
                categoryList.add(AdviceCategory.WARMFLAT)
            }
        }

        if (adviceForecast.temperature >= 18) {
            categoryList.add(AdviceCategory.CAR)
        }

        if (adviceForecast.temperature > 23 && adviceForecast.temperature <= 30) {
            categoryList.add(AdviceCategory.VERYWARM)

            /*if (typeOfDog == WHITENAKED) {
                categoryList.add(AdviceCategory.WHITENAKED)
            }

             */
        }

        if (adviceForecast.temperature > 30) {
            categoryList.add(AdviceCategory.)
        }

         */


        if (categoryList.size == 0) {
            categoryList.add(AdviceCategory.SAFE)
        }

        return categoryList
    }
}