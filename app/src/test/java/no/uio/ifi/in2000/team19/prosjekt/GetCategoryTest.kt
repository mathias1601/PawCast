package no.uio.ifi.in2000.team19.prosjekt

import no.uio.ifi.in2000.team19.prosjekt.data.AdviceFunctions
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime


class GetCategoryTest {
    private lateinit var adviceFunctions: AdviceFunctions

    @Test
    fun getCategory_forVeryWarmWeather_flatNosed_IsCorrect() {

        // Arrange
        val warmAdviceForecast =
            AdviceForecast(
                temperature = 29.1,
                thunderprobability = 54.2,
                precipitation = 0.2,
                UVindex = 4.1,
                date = LocalDateTime.of(2024, 7, 4, 0, 0),
                time = "14")

        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isAdult = true,
            isPuppy = false,
            isFlatNosed = true,
            isNormalNosed = false,
            isThin = false,
            isMediumBody = false,
            isThickBody = true,
            isLongHaired = false,
            isShortHaired = true,
            isThinHaired = false,
            isThickHaired = false,
            isLightHaired = true,
            isDarkHaired = false,
        )

        // Act
        val categoryList = listOf(
            AdviceCategory.SUNBURN,
            AdviceCategory.VERYWARMFLAT,
            AdviceCategory.CAR,
            AdviceCategory.THUNDER,
            AdviceCategory.TICK,
            AdviceCategory.VIPER
        ).toSet()

        val result = adviceFunctions.getCategory(warmAdviceForecast, userInfo).toSet()

        // Assert
        assertEquals(categoryList, result)
    }

    @Test
    fun getCategory_forWarmWeather_flatNosed_IsWrong() {

        // Arrange
        val adviceForecast =
            AdviceForecast(
                temperature = 18.9,
                thunderprobability = 0.0,
                precipitation = 0.1,
                UVindex = 3.5,
                date = LocalDateTime.of(2024, 11, 19, 0, 0),
                time = "18")


        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isAdult = true,
            isPuppy = false,
            isFlatNosed = true,
            isNormalNosed = false,
            isThin = false,
            isMediumBody = false,
            isThickBody = true,
            isLongHaired = false,
            isShortHaired = true,
            isThinHaired = false,
            isThickHaired = false,
            isLightHaired = true,
            isDarkHaired = false,
        )

        // Act
        val categoryList = listOf(
            AdviceCategory.SUNBURN,
            AdviceCategory.WARM,
            AdviceCategory.CAR,
        ).toSet()

        val result = adviceFunctions.getCategory(adviceForecast, userInfo).toSet()

        // Assert
        assertNotEquals(categoryList, result)
    }

    @Test
    fun getCategory_forSunburn_thinAndLongHaired_IsCorrect() {

        // Arrange
        val warmAdviceForecast =
            AdviceForecast(
                temperature = 14.5,
                thunderprobability = 0.0,
                precipitation = 0.0,
                UVindex = 5.1,
                date = LocalDateTime.of(2024, 7, 4, 0, 0),
                time = "15")

        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isAdult = true,
            isPuppy = false,
            isFlatNosed = false,
            isNormalNosed = true,
            isThin = false,
            isMediumBody = true,
            isThickBody = false,
            isLongHaired = true,
            isShortHaired = true,
            isThinHaired = true,
            isThickHaired = false,
            isLightHaired = true,
            isDarkHaired = false,
        )

        // Act
        val categoryList = listOf(
            AdviceCategory.SUNBURN,
            AdviceCategory.TICK,
            AdviceCategory.VIPER
        ).toSet()

        val result = adviceFunctions.getCategory(warmAdviceForecast, userInfo).toSet()

        // Assert
        assertEquals(categoryList, result)
    }

    @Test
    fun getCategory_forSunburn_thickAndLongHaired_IsWrong() {

        // Arrange
        val warmAdviceForecast =
            AdviceForecast(
                temperature = 14.5,
                thunderprobability = 0.0,
                precipitation = 0.0,
                UVindex = 5.1,
                date = LocalDateTime.of(2024, 7, 4, 0, 0),
                time = "15")


        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isAdult = true,
            isPuppy = false,
            isFlatNosed = false,
            isNormalNosed = true,
            isThin = false,
            isMediumBody = true,
            isThickBody = false,
            isLongHaired = true,
            isShortHaired = true,
            isThinHaired = true,
            isThickHaired = false,
            isLightHaired = true,
            isDarkHaired = false,
        )

        // Act
        val categoryList = listOf(
            AdviceCategory.SUNBURN,
            AdviceCategory.TICK,
            AdviceCategory.VIPER
        ).toSet()

        val result = adviceFunctions.getCategory(warmAdviceForecast, userInfo).toSet()

        // Assert
        assertNotEquals(categoryList, result)
    }

    @Test
    fun getCategory_forCold_longHairedOther_IsCorrect() {

        // Arrange
        val adviceForecast =
            AdviceForecast(
                temperature = -7.9,
                thunderprobability = 0.0,
                precipitation = 0.0,
                UVindex = 0.0,
                date = LocalDateTime.of(2024, 12, 9, 0, 0),
                time = "20")

        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isAdult = false,
            isPuppy = true,
            isFlatNosed = false,
            isNormalNosed = true,
            isThin = false,
            isMediumBody = true,
            isThickBody = false,
            isLongHaired = true,
            isShortHaired = true,
            isThinHaired = false,
            isThickHaired = true,
            isLightHaired = false,
            isDarkHaired = false,
        )

        // Act
        val categoryList = listOf(
            AdviceCategory.COLDOTHERLONGFUR,
            AdviceCategory.SALT
        ).toSet()

        val result = adviceFunctions.getCategory(adviceForecast, userInfo).toSet()

        // Assert
        assertEquals(categoryList, result)
    }

    @Test
    fun getCategory_newYear_IsCorrect() {

        // Arrange
        val adviceForecast =
            AdviceForecast(
                temperature = 7.3,
                thunderprobability = 0.0,
                precipitation = 0.0,
                UVindex = 0.0,
                date = LocalDateTime.of(2024, 12, 31, 0, 0),
                time = "12")


        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isAdult = true,
            isPuppy = false,
            isFlatNosed = false,
            isNormalNosed = true,
            isThin = false,
            isMediumBody = true,
            isThickBody = false,
            isLongHaired = true,
            isShortHaired = true,
            isThinHaired = true,
            isThickHaired = false,
            isLightHaired = true,
            isDarkHaired = false,
        )

        // Act
        val categoryList = listOf(
            AdviceCategory.NEWYEAR
        ).toSet()

        val result = adviceFunctions.getCategory(adviceForecast, userInfo).toSet()

        // Assert
        assertEquals(categoryList, result)
    }


}



