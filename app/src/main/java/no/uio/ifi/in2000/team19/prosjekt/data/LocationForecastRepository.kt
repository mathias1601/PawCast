package no.uio.ifi.in2000.team19.prosjekt.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.forecastSuper
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
    private val context: Context
)  {


    //TODO find solution for only one API-call
    private suspend fun fetchLocationForecast(latitude: String, longitude: String, height: String): LocationForecast {
        return locationForecastDataSource.getLocationForecast(latitude, longitude, height)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGeneralForecast(latitude: String, longitude: String, height: String, nrHours: Int, nrDays: Int): List<List<forecastSuper>> {

        val forecastLists = mutableListOf<List<forecastSuper>>()

        val locationForecast = fetchLocationForecast(latitude, longitude, height)

        val updatedAt = locationForecast.properties.meta.updated_at
        val dateTime = ZonedDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME)
        val hour = dateTime.toLocalDateTime().withMinute(0).withSecond(0).withNano(0).hour

        val now = LocalDateTime.now()
        val nowRounded = now.truncatedTo(ChronoUnit.HOURS).hour

        var startingHour = nowRounded - hour

        val lastHour = 23 - hour

        val genForecastList = mutableListOf<GeneralForecast>()
        //if (nrHours <= 3)
        for( i in 0 until lastHour) {
            val temperature = locationForecast.properties.timeseries[startingHour].data.instant.details.air_temperature
            val wind = locationForecast.properties.timeseries[startingHour].data.instant.details.wind_speed
            val symbol = locationForecast.properties.timeseries[startingHour].data.next_1_hours.summary.symbol_code

            val time = locationForecast.properties.timeseries[startingHour].time
            val zonedDateTime = ZonedDateTime.parse(time)
            val hourFormatter = DateTimeFormatter.ofPattern("HH")
            val hourAsInt = zonedDateTime.format(hourFormatter)

            val dateFormatter = DateTimeFormatter.ofPattern("MM-dd")
            val date = zonedDateTime.format(dateFormatter)


            val percipitation = locationForecast.properties.timeseries[startingHour].data.next_1_hours.details.precipitation_amount
            val thunderprobability = locationForecast.properties.timeseries[startingHour].data.next_1_hours.details.probability_of_thunder
            val UVindex = locationForecast.properties.timeseries[startingHour].data.instant.details.ultraviolet_index_clear_sky
            genForecastList.add(GeneralForecast(temperature, wind, symbol, hourAsInt, date, percipitation, thunderprobability, UVindex))

            startingHour += 1
        }

        val dayForecastList = getWeatherForecastForDays(locationForecast, nrDays, startingHour)
        val meanHours = getWeatherForecastHours(locationForecast, startingHour, 2)

        forecastLists.add(genForecastList)
        forecastLists.add(dayForecastList)

        return forecastLists
    }

    //Also possible to do this in the same function. An If-check to see if you want to get for days or hours.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecastForDays(locationForecast: LocationForecast, nrDays: Int, startingHour: Int): List<WeatherForDay> {


        //val updatedAt = locationForecast.properties.meta.updated_at
        //val dateTime = ZonedDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME)
        //val hour = dateTime.toLocalDateTime().withMinute(0).withSecond(0).withNano(0).hour

        //var dayInTime = 24 - hour
        var theHour = startingHour

        var warmestTime: Int
        var coldestTime: Int

        val today = LocalDate.now()


        val forecastList = mutableListOf<WeatherForDay>()

        for (i in 0 until nrDays) {

            val thisDay = today.plusDays(i.toLong() + 1)
            val dayOfWeek = thisDay.dayOfWeek
            val dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale("no", "NO"))

            //Klokka 14
            warmestTime = theHour + 14

            //Klokka 02
            coldestTime = theHour + 2

            val temperatureWarm = locationForecast.properties.timeseries[warmestTime].data.instant.details.air_temperature
            val symbolWarm = locationForecast.properties.timeseries[warmestTime].data.next_1_hours.summary.symbol_code
            val temperatureCold = locationForecast.properties.timeseries[coldestTime].data.instant.details.air_temperature

            forecastList.add(WeatherForDay(symbolWarm, dayOfWeekString, temperatureCold, temperatureWarm, ""))

            theHour += 24
        }

        return forecastList
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecastHours(locationForecast: LocationForecast, startHour: Int, nrDays: Int): List<WeatherForDay> {
        //Tar 3 timer om gangen og finner gjennomsnitt og lager et WeatherForDay-object
        val forecastList = mutableListOf<WeatherForDay>()

        var startOfFirstDay = 24 - startHour

        val today = LocalDate.now()

        //One day is 8, two is 16
        for (i in 0 until 16) {
            val temperatureFirstHour = locationForecast.properties.timeseries[startOfFirstDay].data.instant.details.air_temperature
            val temperatureSecondHour = locationForecast.properties.timeseries[startOfFirstDay + 1].data.instant.details.air_temperature
            val temperatureThirdHour = locationForecast.properties.timeseries[startOfFirstDay + 2].data.instant.details.air_temperature

            val meanTemperature = (temperatureFirstHour + temperatureSecondHour + temperatureThirdHour) / 3

            val thisDay = today.plusDays(i.toLong() + 1)
            val dayOfWeek = thisDay.dayOfWeek
            val dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale("no", "NO"))

            val symbolCode = locationForecast.properties.timeseries[startOfFirstDay].data.next_1_hours.summary.symbol_code

            val timeStart = locationForecast.properties.timeseries[startOfFirstDay].time
            val zonedDateTimeStart = ZonedDateTime.parse(timeStart)
            val hourFormat = DateTimeFormatter.ofPattern("HH")
            val startHourAsInt = zonedDateTimeStart.format(hourFormat)

            val timeEnd = locationForecast.properties.timeseries[startOfFirstDay + 1].time
            val zonedDateTimeEnd = ZonedDateTime.parse(timeEnd)
            val hourFormatter = DateTimeFormatter.ofPattern("HH")
            val endHourAsInt = zonedDateTimeEnd.format(hourFormatter)

            forecastList.add(WeatherForDay(symbolCode, dayOfWeekString, null, null, startHourAsInt, endHourAsInt, meanTemperature))

            startOfFirstDay += 3
        }

        return forecastList

    }

    //Returnerer en liste av Advice-objekter
    fun getAdvice(generalForecast: List<List<forecastSuper>>): List<Advice> {


        val firstForecast = generalForecast[0][0]
        val adviceForecast = when (firstForecast) {
            is GeneralForecast -> getAdviceForecastData(firstForecast)
            else -> return emptyList()  // Eller en annen passende feilhåndtering
        }

        val categories = getCategory(adviceForecast, "SMALL")
        return createAdvice(categories)
    }


    //Gjør om fra GeneralForecast til AdviceForecast (fjerner unødvendig dsta)
    private fun getAdviceForecastData(generalForecast: GeneralForecast): AdviceForecast {
        return AdviceForecast(generalForecast.temperature, generalForecast.thunderprobability, generalForecast.percipitation, generalForecast.UVindex, generalForecast.date)
    }


    //Lager AdviceCards, og retunerer en liste av de
    private fun createAdvice(categories: List<AdviceCategory>): List<Advice> {

        var adviceList = mutableListOf<Advice>()

        if (categories[0] == AdviceCategory.SAFE) {
            val safeArray = context.resources.getStringArray(R.array.SAFE)


            val advice = Advice(safeArray[0], safeArray[1], safeArray[2])
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
            if (adviceArray != null) {
                while  (counter < adviceArray.size){

                    val title = adviceArray?.get(counter).toString()
                    val description = adviceArray?.get(counter+1).toString()
                    val shortAdvice = adviceArray?.get(counter+2).toString()

                    val advice = Advice(title, description, shortAdvice)
                    adviceList.add(advice)

                    counter+=3

                }
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
        if (adviceForecast.percipitation >= 0.1) {
            categoryList.add(AdviceCategory.RAIN)
        }

        //Finne datoen i dag og sjekke om den er innenfor en range hvor flått og hoggorm er aktive


        if (categoryList.size == 0) {
            categoryList.add(AdviceCategory.SAFE)
        }

        return categoryList
    }
}