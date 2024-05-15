package no.uio.ifi.in2000.team19.prosjekt

import no.uio.ifi.in2000.team19.prosjekt.data.AdviceFunctions
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.dto.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.dto.GeneralForecast
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime


class AdviceTest {

    @Test
    fun getAdviceForecastDataIsCorrect() {

        // Arrange and act
        val generalForecast = GeneralForecast(
            14.6,
            4.5,
            "partlycloudy",
            "12",
            LocalDateTime.of(2024, 3, 1, 0, 0),
            1.8,
            44.2,
            2.7
        )


        val expectedAdviceForecast =
            AdviceForecast(14.6, 44.2, 1.8, 2.7, LocalDateTime.of(2024, 3, 1, 0, 0), "12")
        val result = AdviceFunctions.getAdviceForecastData(generalForecast)

        // Assert
        assertEquals(expectedAdviceForecast, result)
    }

    @Test
    fun getCategory_forVeryWarmWeather_flatNosed_IsCorrect() {

        // Arrange
        val warmAdviceForecast =
            AdviceForecast(
                temperature = 29.1,
                thunderProbability = 54.2,
                precipitation = 0.2,
                uvIndex = 4.1,
                date = LocalDateTime.of(2024, 7, 4, 0, 0),
                time = "14"
            )

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
            isDarkHaired = false,
            isAdult = false,
            isMediumBody = false,
            isNormalNosed = false,
            isThickBody = false
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

        val result = AdviceFunctions.getCategory(warmAdviceForecast, userInfo).toSet()

        // Assert
        assertEquals(categoryList, result)
    }
}



