package no.uio.ifi.in2000.team19.prosjekt.data

import android.content.Context
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import java.time.LocalDateTime


fun getAdviceForecastData(generalForecast: GeneralForecast): AdviceForecast {

    return AdviceForecast(
        generalForecast.temperature,
        generalForecast.thunderprobability,
        generalForecast.precipitation,
        generalForecast.UVindex,
        generalForecast.date,
        generalForecast.hour
    )
}

fun getCategory(adviceForecast: AdviceForecast, typeOfDog: UserInfo): List<AdviceCategory> {

    val categoryList = mutableListOf<AdviceCategory>()

    fun isTemperatureInRange(limits: List<Double>, temp: Double): Boolean {
        return temp in limits[0]..limits[1]
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
    //Find special categories and overwrite by removing old category/categories
    /*
    when {
        typeOfDog.isThin || typeOfDog.isPuppy || typeOfDog.isShortHaired || typeOfDog.isSenior || typeOfDog.isThinHaired -> {
            if (AdviceCategory.COOL in categoryList) {
                categoryList.add(AdviceCategory.COOLOTHER)
                categoryList.remove(AdviceCategory.COOL)}
            if (AdviceCategory.COLD in categoryList) {
                categoryList.add(AdviceCategory.COLDOTHER)
                categoryList.remove(AdviceCategory.COLD)}
        }

        typeOfDog.isFlatNosed -> {
            if (AdviceCategory.WARM in categoryList) {
                categoryList.add(AdviceCategory.WARMFLAT)
                categoryList.remove(AdviceCategory.WARM)}
            if (AdviceCategory.VERYWARM in categoryList) {
                categoryList.add(AdviceCategory.VERYWARMFLAT)
                categoryList.remove(AdviceCategory.VERYWARM)}
        }

        typeOfDog.isLongHaired && (AdviceCategory.COLD in categoryList || AdviceCategory.COLDOTHER in categoryList) -> {
            categoryList.add(AdviceCategory.COLDLONGFUR)
            categoryList.remove(AdviceCategory.COLD)
            if (AdviceCategory.COLDOTHER in categoryList) {
                categoryList.remove(AdviceCategory.COLDOTHER)
            }

        }

        typeOfDog.isLongHaired && AdviceCategory.COLD in categoryList -> {
            categoryList.add(AdviceCategory.COLDLONGFUR)

            if (AdviceCategory.VERYWARM in categoryList) {
                categoryList.add(AdviceCategory.VERYWARMFLAT)
            }

        }
    }
*/

    if (typeOfDog.isThin ||
        typeOfDog.isPuppy ||
        typeOfDog.isShortHaired ||
        typeOfDog.isSenior ||
        typeOfDog.isThinHaired) {

        if (AdviceCategory.COOL in categoryList) {
            categoryList.remove(AdviceCategory.COOL)
            categoryList.add(AdviceCategory.COOLOTHER)
        }

        if (AdviceCategory.COLD in categoryList) {
            categoryList.remove(AdviceCategory.COLD)
            categoryList.add(AdviceCategory.COLDOTHER)
        }
    }


    if (typeOfDog.isFlatNosed) {

        if (AdviceCategory.WARM in categoryList) {
            categoryList.remove(AdviceCategory.WARM)
            categoryList.add(AdviceCategory.WARMFLAT)
        }

        if (AdviceCategory.VERYWARM in categoryList) {
            categoryList.remove(AdviceCategory.VERYWARM)
            categoryList.add(AdviceCategory.VERYWARMFLAT)
        }
    }

    if (typeOfDog.isLongHaired)
        if (AdviceCategory.COLD in categoryList) {
            categoryList.remove(AdviceCategory.COLD)
            categoryList.add(AdviceCategory.COLDLONGFUR)
        }

        else if (AdviceCategory.COLDOTHER in categoryList) {
            categoryList.remove(AdviceCategory.COLDOTHER)
            categoryList.add(AdviceCategory.COLDOTHERLONGFUR)
        }


    if (adviceForecast.UVindex >= 3 && (
                typeOfDog.isThinHaired ||
                        typeOfDog.isLightHaired ||
                        typeOfDog.isShortHaired
                )
    ) {
        categoryList.add(AdviceCategory.SUNBURN)
    }

    //TODO find right number
    if (adviceForecast.thunderprobability >= 50) {
        categoryList.add(AdviceCategory.THUNDER)
    }

    //TODO find right number
    if (adviceForecast.precipitation >= 1) {
        categoryList.add(AdviceCategory.RAIN)
    }

    //TODO lage en when for dato for flått, hoggorm og nyttår

    val tickSeasonStart = LocalDateTime.of(2024, 3, 15, 0, 0
    )
    // Year, Month, Day, Hour, Minute (defaults to 00:00)

    val tickSeasonEnd = LocalDateTime.of(2024, 11, 15, 0, 0)

    val viperSeasonStart = LocalDateTime.of(2024, 2, 28, 0, 0)
    val viperSeasonEnd = LocalDateTime.of(2024, 11, 1, 0, 0)

    val newYear = LocalDateTime.of(2024, 12, 31, 0, 0)

    val theDate = adviceForecast.date


    if (!theDate.isBefore(tickSeasonStart) && !theDate.isAfter(tickSeasonEnd)) {
        categoryList.add(AdviceCategory.TICK)
    }

    if (!theDate.isBefore(viperSeasonStart) && !theDate.isAfter(viperSeasonEnd)) {
        categoryList.add(AdviceCategory.VIPER)
    }

    if (theDate == newYear) {
        categoryList.add(AdviceCategory.NEWYEAR)
    }


    if (categoryList.size == 0) {
        categoryList.add(AdviceCategory.SAFE)
    }

    return categoryList
}

fun createAdvice(categories: List<AdviceCategory>, context: Context): List<Advice> {

    val adviceList = mutableListOf<Advice>()

    if (categories[0] == AdviceCategory.SAFE) {
        val safeArray = context.resources.getStringArray(R.array.SAFE)


        val advice = Advice(safeArray[0], safeArray[1], safeArray[2])
        adviceList.add(advice)
        return adviceList
    }

    categories.forEach { category ->

        var adviceArray: Array<String>? = null
        //val resId = context.resources.getIdentifier(category.toString(), "array", context.packageName)
        //val adviceArray: Array<String> = context.resources.getStringArray(resId)
        when (category.toString()) {
            "COOL" -> adviceArray = context.resources.getStringArray(R.array.COOL)
            "COOLOTHER" -> adviceArray = context.resources.getStringArray(R.array.COOLOTHER)
            "COLD" -> adviceArray = context.resources.getStringArray(R.array.COLD)
            "COLDLONGFUR" -> adviceArray = context.resources.getStringArray(R.array.COLDLONGFUR)
            "COLDOTHER" -> adviceArray = context.resources.getStringArray(R.array.COLDOTHER)
            "COLDOTHERLONGFUR" -> adviceArray = context.resources.getStringArray(R.array.COLDOTHERLONGFUR)
            "FREEZING" -> adviceArray = context.resources.getStringArray(R.array.FREEZING)
            "SALT" -> adviceArray = context.resources.getStringArray(R.array.SALT)
            "WARM" -> adviceArray = context.resources.getStringArray(R.array.WARM)
            "WARMFLAT" -> adviceArray = context.resources.getStringArray(R.array.WARMFLAT)
            "VERYWARM" -> adviceArray = context.resources.getStringArray(R.array.VERYWARM)
            "VERYWARMFLAT" -> adviceArray =
                context.resources.getStringArray(R.array.VERYWARMFLAT)

            "HEATWAVE" -> adviceArray = context.resources.getStringArray(R.array.HEATWAVE)
            "RAIN" -> adviceArray = context.resources.getStringArray(R.array.RAIN)
            "THUNDER" -> adviceArray = context.resources.getStringArray(R.array.THUNDER)
            "SUNBURN" -> adviceArray = context.resources.getStringArray(R.array.SUNBURN)
            "TICK" -> adviceArray = context.resources.getStringArray(R.array.TICK)
            "VIPER" -> adviceArray = context.resources.getStringArray(R.array.VIPER)
            "CAR" -> adviceArray = context.resources.getStringArray(R.array.CAR)


        }

        var counter = 0
        if (adviceArray != null) {
            while (counter < adviceArray.size) {

                val title = adviceArray[counter]
                val description = adviceArray[counter + 1]
                val shortAdvice = adviceArray[counter + 2]

                val advice = Advice(title, description, shortAdvice)
                adviceList.add(advice)

                counter += 3

            }
        }
    }

    return adviceList
}