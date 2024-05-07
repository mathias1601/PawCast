package no.uio.ifi.in2000.team19.prosjekt.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.ForecastTypes
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.locationForecast.LocationForecast
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.MonthDay
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


    fun getAdviceForecastList(listOfGeneralForecasts: ForecastTypes): List<AdviceForecast> {

        val adviceForecasts = mutableListOf<AdviceForecast>()
        val general: List<GeneralForecast> = listOfGeneralForecasts.general
        general.forEach{
            adviceForecasts.add(getAdviceForecastData(it))
        }
        return adviceForecasts
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGeneralForecast(latitude: String, longitude: String, height: String, nrDays: Int): ForecastTypes {

        val locationForecast = fetchLocationForecast(latitude, longitude, height)

        val start = locationForecast.properties.timeseries[0].time
        val dateTime = ZonedDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME)
        val startHour = dateTime.toLocalDateTime().truncatedTo(ChronoUnit.HOURS)

        val now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)

        val hours = ChronoUnit.HOURS.between(startHour, now)
        Log.i("Debugger", "hours aka antall timer mellom starten og nå $hours")

        //If the time is past midnight, hours will get a negative value
        val adjustedStart = if (hours < 0) 24 + hours.toInt() else hours.toInt()
        Log.i("Debugger", "adjusted: $adjustedStart")


        //Find ending point of for-loop.

        val hoursTo23 = (23 - now.hour + 24) % 24 + adjustedStart
        Log.i("Debugger", "hoursTo23 $hoursTo23")
        val startOfNextDay = hoursTo23 + 1
        Log.i("Debugger", "startOfNextDay $startOfNextDay")

        val lastHour = (hoursTo23 + 12)

        val genForecastList = mutableListOf<GeneralForecast>()
        //if (nrHours <= 3)
        for( i in adjustedStart .. lastHour) {
            val temperature = locationForecast.properties.timeseries[i].data.instant.details.air_temperature
            val wind = locationForecast.properties.timeseries[i].data.instant.details.wind_speed
            val symbol = locationForecast.properties.timeseries[i].data.next_1_hours.summary.symbol_code

            val time = locationForecast.properties.timeseries[i].time
            val zonedDateTime = ZonedDateTime.parse(time)
            val hourFormatter = DateTimeFormatter.ofPattern("HH")
            val hourAsInt = zonedDateTime.format(hourFormatter)

            val dateFormatter = DateTimeFormatter.ofPattern("MM-dd")
            val date = zonedDateTime.format(dateFormatter)


            val percipitation = locationForecast.properties.timeseries[i].data.next_1_hours.details.precipitation_amount
            val thunderprobability = locationForecast.properties.timeseries[i].data.next_1_hours.details.probability_of_thunder
            val UVindex = locationForecast.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky

            genForecastList.add(GeneralForecast(temperature, wind, symbol, hourAsInt, date, percipitation, thunderprobability, UVindex, time))

            //startingHour += 1
        }

        val dayForecastList = getWeatherForecastForDays(locationForecast, nrDays, startOfNextDay)
        val meanHours = getWeatherForecastHours(locationForecast, startOfNextDay)

        val forecasts = ForecastTypes(genForecastList, dayForecastList, meanHours)

        return forecasts
    }

    //Also possible to do this in the same function. An If-check to see if you want to get for days or hours.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecastForDays(locationForecast: LocationForecast, nrDays: Int, startingHour: Int): List<WeatherForDay> {

        //var dayInTime = 24 - hour
        var theHour = startingHour

        Log.i("Debugger", "theHour $theHour")

        var middleOfDay: Int
        //var coldestTime: Int

        val today = LocalDate.now()


        val forecastList = mutableListOf<WeatherForDay>()

        for (i in 0 until nrDays) {

            val thisDay = today.plusDays(i.toLong() + 1)
            val dayOfWeek = thisDay.dayOfWeek
            val dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale("no", "NO"))

            //Klokka 14
            middleOfDay = theHour + 14

            val temperatures = mutableListOf<Double>()

            for (j in theHour .. theHour + 23) {
                temperatures.add(locationForecast.properties.timeseries[j].data.instant.details.air_temperature)
            }

            val warmestTemperature = temperatures.max()
            val coldestTemperature = temperatures.min()

            //val temperatureWarm = locationForecast.properties.timeseries[warmestTime].data.instant.details.air_temperature
            val nextHoursData = locationForecast.properties.timeseries[middleOfDay].data.next_1_hours
            val symbolCode = nextHoursData?.summary?.symbol_code ?: locationForecast.properties.timeseries[middleOfDay].data.next_6_hours.summary.symbol_code
            //val symbolWarm = locationForecast.properties.timeseries[middleOfDay].data.next_1_hours.summary.symbol_code
            //val temperatureCold = locationForecast.properties.timeseries[coldestTime].data.instant.details.air_temperature

            forecastList.add(WeatherForDay(symbolCode, dayOfWeekString, coldestTemperature, warmestTemperature))

            theHour += 24
        }

        return forecastList
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherForecastHours(locationForecast: LocationForecast, startHour: Int): List<WeatherForDay> {
        //Tar 3 timer om gangen og finner gjennomsnitt og lager et WeatherForDay-object
        val forecastList = mutableListOf<WeatherForDay>()

        var theHour = startHour

        val today = LocalDate.now()
        var nextDay = today.plusDays(1)

        //One day is 8, two is 16
        for (i in 0 until 8) {

            val timeStart = locationForecast.properties.timeseries[theHour].time
            val timeSecond = locationForecast.properties.timeseries[theHour + 1].time
            //val zonedDateTimeStart = ZonedDateTime.parse(timeStart)
            //val hourFormat = DateTimeFormatter.ofPattern("HH")
            //val startHourAsInt = zonedDateTimeStart.format(hourFormat)

            val timeEnd = locationForecast.properties.timeseries[theHour + 6].time
            //val zonedDateTimeEnd = ZonedDateTime.parse(timeEnd)
            //val hourFormatter = DateTimeFormatter.ofPattern("HH")
            //val endHourAsInt = zonedDateTimeEnd.format(hourFormatter)

            val symbolCode = locationForecast.properties.timeseries[theHour].data.next_6_hours.summary.symbol_code
            val percipitation = locationForecast.properties.timeseries[theHour].data.next_6_hours.details.precipitation_amount


            val lastHour = timeEnd.substring(11, 13)
            val start = timeStart.substring(11, 13)
            val secondHour = timeSecond.substring(11, 13)

            val endHour: String

            val startHourInt = start.toInt()
            val secondHourInt = secondHour.toInt()

            val hoursBetween = if (startHourInt <= secondHourInt) {
                secondHourInt - startHourInt
            } else {
                (24 - startHourInt + secondHourInt)
            }

            var meanTemperature: Double
            val meanWind: Double
            //val meanPercipitation: Double

            Log.d("Debug vær", "hours between $start and $secondHour = $hoursBetween")

            //Sometimes the differnece between two timeseries is 6 hours instead of one
            if (hoursBetween == 6) {
                meanTemperature = (locationForecast.properties.timeseries[theHour].data.instant.details.air_temperature +
                        locationForecast.properties.timeseries[theHour + 1].data.instant.details.air_temperature) / 2
                Log.d("Debug vær", "$hoursBetween slo inn")


                meanWind = (locationForecast.properties.timeseries[theHour].data.instant.details.wind_speed +
                        locationForecast.properties.timeseries[theHour + 1].data.instant.details.wind_speed) / 2


                //meanPercipitation = (locationForecast.properties.timeseries[startOfFirstDay].data.next_1_hours.details.precipitation_amount +
                //        locationForecast.properties.timeseries[startOfFirstDay + 1].data.next_1_hours.details.precipitation_amount) / 2

                endHour = secondHour
            }
            else {

                meanTemperature = locationForecast.properties.timeseries[theHour].data.next_6_hours.details.air_temperature_max
                   /* (locationForecast.properties.timeseries[theHour].data.instant.details.air_temperature +
                            locationForecast.properties.timeseries[theHour + 1].data.instant.details.air_temperature +
                            locationForecast.properties.timeseries[theHour + 2].data.instant.details.air_temperature +
                            locationForecast.properties.timeseries[theHour + 3].data.instant.details.air_temperature) / 4


                    */

                //TODO might use next_6_hours highest and lowest temperature from locationforecast then theres no need to repeat

                /*repeat(6) {
                    meanTemperature += locationForecast.properties.timeseries[theHour].data.instant.details.air_temperature
                    theHour += 1
                }

                 */

                meanWind =
                    (locationForecast.properties.timeseries[theHour].data.instant.details.wind_speed +
                            locationForecast.properties.timeseries[theHour + 1].data.instant.details.wind_speed +
                            locationForecast.properties.timeseries[theHour + 2].data.instant.details.wind_speed +
                            locationForecast.properties.timeseries[theHour + 3].data.instant.details.wind_speed) / 4


                /*meanPercipitation =
                    (locationForecast.properties.timeseries[startOfFirstDay].data.next_1_hours.details.precipitation_amount +
                            locationForecast.properties.timeseries[startOfFirstDay + 1].data.next_1_hours.details.precipitation_amount +
                            locationForecast.properties.timeseries[startOfFirstDay + 2].data.next_1_hours.details.precipitation_amount +
                            locationForecast.properties.timeseries[startOfFirstDay + 3].data.next_1_hours.details.precipitation_amount / 4)

                 */

                endHour = lastHour
            }


            val roundedTemperature = String.format("%.1f", meanTemperature)
            val roundedWind = String.format("%.1f", meanWind)
            //val roundedPrecepitation = String.format("%.1f", meanPercipitation)


            if (i == 4) {
                nextDay = nextDay.plusDays( 1)
            }

            val dayOfWeek = nextDay.dayOfWeek
            val dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale("no", "NO"))


            forecastList.add(WeatherForDay(symbolCode, dayOfWeekString, null, null, start, endHour, roundedTemperature, roundedWind, "12.1"))

            theHour += 6
        }

        return forecastList

    }

    //Returnerer en liste av Advice-objekter
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAdvice(generalForecast: ForecastTypes, typeOfDog: UserInfo): List<Advice> {

        val adviceForecast = getAdviceForecastData(generalForecast.general[0])

        val categories = getCategory(adviceForecast, typeOfDog)

        return createAdvice(categories)
    }


    //Gjør om fra GeneralForecast til AdviceForecast (fjerner unødvendig dsta)
    private fun getAdviceForecastData(generalForecast: GeneralForecast): AdviceForecast {

        return AdviceForecast(generalForecast.temperature, generalForecast.thunderprobability, generalForecast.percipitation, generalForecast.UVindex, generalForecast.date, generalForecast.hour)
    }


    //Lager AdviceCards, og retunerer en liste av de
    private fun createAdvice(categories: List<AdviceCategory>): List<Advice> {

        val adviceList = mutableListOf<Advice>()

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
                "COOL" -> adviceArray = context.resources.getStringArray(R.array.COOL)
                "COOLOTHER" -> adviceArray = context.resources.getStringArray(R.array.COOLOTHER)
                "COLD" -> adviceArray = context.resources.getStringArray(R.array.COLD)
                "COLDLONGFUR" -> adviceArray = context.resources.getStringArray(R.array.COLDLONGFUR)
                "COLDOTHER" -> adviceArray = context.resources.getStringArray(R.array.COLDOTHER)
                "FREEZING" -> adviceArray = context.resources.getStringArray(R.array.FREEZING)
                "SALT" -> adviceArray = context.resources.getStringArray(R.array.SALT)
                "WARM" -> adviceArray = context.resources.getStringArray(R.array.WARM)
                "WARMFLAT" -> adviceArray = context.resources.getStringArray(R.array.WARMFLAT)
                "VERYWARM" -> adviceArray = context.resources.getStringArray(R.array.VERYWARM)
                "VERYWARMFLAT" -> adviceArray = context.resources.getStringArray(R.array.VERYWARMFLAT)
                "HEATWAVE" -> adviceArray = context.resources.getStringArray(R.array.HEATWAVE)
                "RAIN" -> adviceArray = context.resources.getStringArray(R.array.RAIN)
                "THUNDER" -> adviceArray = context.resources.getStringArray(R.array.THUNDER)
                "SNOW" -> adviceArray = context.resources.getStringArray(R.array.SNOW)
                "SUNBURN" -> adviceArray = context.resources.getStringArray(R.array.SUNBURN)
                "TICK" -> adviceArray = context.resources.getStringArray(R.array.TICK)
                "VIPER" -> adviceArray = context.resources.getStringArray(R.array.VIPER)
                "CAR" -> adviceArray = context.resources.getStringArray(R.array.CAR)


            }

            var counter = 0
            if (adviceArray != null) {
                while  (counter < adviceArray.size){

                    val title = adviceArray.get(counter).toString()
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

    // Refactored Code
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCategory(adviceForecast: AdviceForecast, typeOfDog: UserInfo): List<AdviceCategory> {
        // Early return if typeOfDog is null or adviceForecast has no categories


        //kunne returnert en tom liste tidlig, men dette er ikke nødvendig fordi parameterne ikke
        //kan være tomme, og fordi funksjonen som er avhengig av getCategory trenger at lista
        //er fylt med noe - som da er safe kategorien hvis ingen ting slår inn

        val categoryList = mutableListOf<AdviceCategory>()

        // Define a function to check temperature ranges based on category
        fun isTemperatureInRange(limits: List<Double>, temp: Double): Boolean {
            return temp in limits[0] .. limits[1]
        }

        // Map of category-specific temperatures to check against
        val weatherLimitsMap = mapOf(
            AdviceCategory.COOL to listOf(-5.0, 0.0),
            AdviceCategory.COLD to listOf(-15.0, -5.0),
            AdviceCategory.FREEZING to listOf(-70.0, -15.0),
            AdviceCategory.SALT to listOf(-8.0, 4.0),
            AdviceCategory.WARM to listOf(15.0, 23.0),
            AdviceCategory.VERYWARM to listOf(23.0, 30.0),
            AdviceCategory.HEATWAVE to listOf(30.0, 70.0),
            AdviceCategory.CAR to listOf(18.0, 70.0)
        )


        weatherLimitsMap.forEach { (category, limits) ->
            if (isTemperatureInRange(limits, adviceForecast.temperature)) {
                categoryList.add(category)
            }
        }
            //refactored based on result from KotlinRefactorer. Used to be multiple if checks
            when {
                typeOfDog.isThin || typeOfDog.isPuppy || typeOfDog.isShortHaired || typeOfDog.isSenior || typeOfDog.isThinHaired -> {
                    if (AdviceCategory.COOL in categoryList) { categoryList.add(AdviceCategory.COOLOTHER)}
                    if (AdviceCategory.COLD in categoryList) { categoryList.add(AdviceCategory.COLDOTHER)}
                }

                typeOfDog.isFlatNosed -> {
                    if (AdviceCategory.WARM in categoryList) {categoryList.add(AdviceCategory.WARMFLAT)}
                    if (AdviceCategory.VERYWARM in categoryList) {categoryList.add(AdviceCategory.VERYWARMFLAT)}
                }

                typeOfDog.isLongHaired && AdviceCategory.COLD in categoryList -> {
                    categoryList.add(AdviceCategory.COLDLONGFUR)
                }
            }



        if (adviceForecast.UVindex >= 3 && (
                    typeOfDog.isThinHaired  ||
                    typeOfDog.isLightHaired ||
                    typeOfDog.isShortHaired
                )
            )
        {
            categoryList.add(AdviceCategory.SUNBURN)
        }

        //TODO find right number
        if (adviceForecast.thunderprobability >= 50) {
            categoryList.add(AdviceCategory.THUNDER)
            Log.i("KATEGORIER", "Legger til thunder")
        }

        //TODO find right number
        if (adviceForecast.percipitation >= 0.1) {
            categoryList.add(AdviceCategory.RAIN)
        }

        //TODO lage en when for dato for flått, hoggorm og nyttår


        if (categoryList.size == 0) {
            categoryList.add(AdviceCategory.SAFE)
        }

        return categoryList
    }
/*

    ### Changes Made:
    1. **Early Return**: Simplified the condition by using `?:` operator which returns early if `typeOfDog` is not present or any other condition is met. This reduces nesting and makes the code more readable.

    2. **Function Extraction**: Created a separate function `isTemperatureInRange` to encapsulate the logic for checking temperature ranges, making the main function cleaner and easier to understand.

    3. **Use of Destructuring Declarations**: Utilized destructuring declarations (`a to b`) to create pairs from tuples directly within the map initialization, reducing verbosity.

    4. **Filter Not Nulls**: Replaced explicit null checks with `?.let` which simplifies the conditional logic and avoids unnecessary branching.

    5. **Simplify Conditional Logic**: Combined similar conditions into single lines where possible to reduce cyclomatic complexity.

    6. **Consolidation of Category Additions**: Grouped related operations together such as adding `COOLOTHER`, `COLDOTHER`, etc., to make the code more concise and easier to read.

    7. **Removed Redundant Comments**: Removed TODO comments that were resolved during refactoring process.

    By applying these changes, the code should have reduced cyclomatic complexity due to fewer branches, improved maintainability due to better organization and readability, and potentially lower Halstead Effort due to less overall code volume and complexity.
*/
}


