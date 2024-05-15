package no.uio.ifi.in2000.team19.prosjekt

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastDataSource
import no.uio.ifi.in2000.team19.prosjekt.model.dto.locationForecast.LocationForecast
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class LocationDataSourceTest {



    @Test
    /** Mostly just tests that the API actually works, but also
     * tests that behaviour is as expected.
     *
     * A better test should mock clients and more: https://ktor.io/docs/client-testing.html#add_dependencies
     * + technical debt on writing better tests for datasource.
     *
     * But its also nice to test the ifi proxy / MET api in our opinion.
     * */
    fun get_valid_result_with_valid_coordinates(){

        val dataSource = LocationForecastDataSource()

        // ARRANGE
        val latitude = "50"
        val longitude = "50"
        var apiResult : LocationForecast?


        // ACT
        runBlocking {
            apiResult = dataSource.getLocationForecast(latitude, longitude)
        }
        // ASSERT
        assertNotEquals(null, apiResult)

    }

    @Test
    fun get_null_with_invalid_coordinates(){

        val dataSource = LocationForecastDataSource()

        // ARRANGE
        val latitude = "0"
        val longitude = "9999"
        var apiResult : LocationForecast?


        // ACT
        runBlocking {
            apiResult = dataSource.getLocationForecast(latitude, longitude)
        }
        // ASSERT
        assertEquals(null, apiResult)

    }

}