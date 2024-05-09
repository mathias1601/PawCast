package no.uio.ifi.in2000.team19.prosjekt.data

import android.util.Log
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import java.time.LocalDate


fun getAdviceForecastData(generalForecast: GeneralForecast): AdviceForecast {

    return AdviceForecast(
        generalForecast.temperature,
        generalForecast.thunderprobability,
        generalForecast.percipitation,
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

    if (typeOfDog.isLongHaired && AdviceCategory.COLD in categoryList) {
        categoryList.remove(AdviceCategory.COLD)
        categoryList.add(AdviceCategory.COLDLONGFUR)
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
    if (adviceForecast.percipitation >= 1) {
        categoryList.add(AdviceCategory.RAIN)
    }

    //TODO lage en when for dato for flått, hoggorm og nyttår

    val tickSeasonStart = LocalDate.of(2024, 3, 15)
    val tickSeasonEnd = LocalDate.of(2024, 11, 15)

    val viperSeasonStart = LocalDate.of(2024, 2, 28)
    val viperSeasonEnd = LocalDate.of(2024, 11, 1)

    val newYear = LocalDate.of(2024, 12, 31)

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