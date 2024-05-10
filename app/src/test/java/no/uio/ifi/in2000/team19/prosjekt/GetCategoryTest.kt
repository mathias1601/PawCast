package no.uio.ifi.in2000.team19.prosjekt

import no.uio.ifi.in2000.team19.prosjekt.data.getCategory
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate


class GetCategoryTest {

    @Test
    fun getCategory_forVeryWarmWeather_flatNosed_IsCorrect() {

        // Arrange
        val warmAdviceForecast =
            AdviceForecast(
                temperature = 29.1,
                thunderprobability = 54.2,
                percipitation = 0.2,
                UVindex = 4.1,
                date = LocalDate.of(2024, 7, 4),
                time = "14")

        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isPuppy = false,
            isFlatNosed = true,
            isThin = false,
            isLongHaired = false,
            isShortHaired = true,
            isThinHaired = true,
            isThickHaired = false,
            isLightHaired = true,
            isDarkHaired = false
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

        val result = getCategory(warmAdviceForecast, userInfo).toSet()

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
                percipitation = 0.1,
                UVindex = 3.5,
                date = LocalDate.of(2024, 11, 19),
                time = "18")

        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isPuppy = false,
            isFlatNosed = true,
            isThin = false,
            isLongHaired = false,
            isShortHaired = true,
            isThinHaired = true,
            isThickHaired = false,
            isLightHaired = true,
            isDarkHaired = false
        )

        // Act
        val categoryList = listOf(
            AdviceCategory.SUNBURN,
            AdviceCategory.WARM,
            AdviceCategory.CAR,
        ).toSet()

        val result = getCategory(adviceForecast, userInfo).toSet()

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
                percipitation = 0.0,
                UVindex = 5.1,
                date = LocalDate.of(2024, 7, 4),
                time = "15")

        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isPuppy = false,
            isFlatNosed = false,
            isThin = false,
            isLongHaired = true,
            isShortHaired = false,
            isThinHaired = true,
            isThickHaired = false,
            isLightHaired = false,
            isDarkHaired = false
        )

        // Act
        val categoryList = listOf(
            AdviceCategory.SUNBURN,
            AdviceCategory.TICK,
            AdviceCategory.VIPER
        ).toSet()

        val result = getCategory(warmAdviceForecast, userInfo).toSet()

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
                percipitation = 0.0,
                UVindex = 5.1,
                date = LocalDate.of(2024, 7, 4),
                time = "15")

        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isPuppy = false,
            isFlatNosed = false,
            isThin = false,
            isLongHaired = true,
            isShortHaired = false,
            isThinHaired = false,
            isThickHaired = true,
            isLightHaired = false,
            isDarkHaired = false
        )

        // Act
        val categoryList = listOf(
            AdviceCategory.SUNBURN,
            AdviceCategory.TICK,
            AdviceCategory.VIPER
        ).toSet()

        val result = getCategory(warmAdviceForecast, userInfo).toSet()

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
                percipitation = 0.0,
                UVindex = 0.0,
                date = LocalDate.of(2024, 12, 9),
                time = "20")

        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isPuppy = true,
            isFlatNosed = false,
            isThin = false,
            isLongHaired = true,
            isShortHaired = false,
            isThinHaired = false,
            isThickHaired = true,
            isLightHaired = false,
            isDarkHaired = false
        )

        // Act
        val categoryList = listOf(
            AdviceCategory.COLDOTHERLONGFUR,
            AdviceCategory.SALT
        ).toSet()

        val result = getCategory(adviceForecast, userInfo).toSet()

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
                percipitation = 0.0,
                UVindex = 0.0,
                date = LocalDate.of(2024, 12, 31),
                time = "12")

        val userInfo = UserInfo(
            1,
            "Per",
            "Bella",
            isSenior = false,
            isPuppy = false,
            isFlatNosed = false,
            isThin = false,
            isLongHaired = true,
            isShortHaired = false,
            isThinHaired = false,
            isThickHaired = true,
            isLightHaired = false,
            isDarkHaired = false
        )

        // Act
        val categoryList = listOf(
            AdviceCategory.NEWYEAR
        ).toSet()

        val result = getCategory(adviceForecast, userInfo).toSet()

        // Assert
        assertEquals(categoryList, result)
    }


}



