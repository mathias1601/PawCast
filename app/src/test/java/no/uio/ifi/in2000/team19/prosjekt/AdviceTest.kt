package no.uio.ifi.in2000.team19.prosjekt

import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.getAdviceForecastData
import no.uio.ifi.in2000.team19.prosjekt.data.getCategory
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate


class AdviceTest {

    @Test
    fun getAdviceForecastDataIsCorrect() {

        // Arrange and act
        val generalForecast = GeneralForecast(
            14.6,
            4.5,
            "partlycloudy",
            "12",
            LocalDate.of(2024, 3, 1),
            1.8,
            44.2,
            2.7
        )


        val expectedAdviceForecast =
            AdviceForecast(14.6, 44.2, 1.8, 2.7, LocalDate.of(2024, 3, 1), "12")
        val result = getAdviceForecastData(generalForecast)

        // Assert
        assertEquals(expectedAdviceForecast, result)
    }

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
}



