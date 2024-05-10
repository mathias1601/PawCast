package no.uio.ifi.in2000.team19.prosjekt

import no.uio.ifi.in2000.team19.prosjekt.data.getAdviceForecastData
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate

class GetAdviceForecastDataTest {

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
        Assert.assertEquals(expectedAdviceForecast, result)
    }
}