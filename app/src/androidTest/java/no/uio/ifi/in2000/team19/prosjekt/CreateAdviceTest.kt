package no.uio.ifi.in2000.team19.prosjekt

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import no.uio.ifi.in2000.team19.prosjekt.data.AdviceFunctions
import no.uio.ifi.in2000.team19.prosjekt.data.createAdvice
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class CreateAdviceTest {
    private lateinit var adviceFunctions: AdviceFunctions

    @Test
    fun createAdvice_Safe() {


        // Arrange
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val categories = listOf(
            AdviceCategory.SAFE,
        )

        // Act
        val expectedStrings = appContext.resources.getStringArray(R.array.SAFE)
        val expectedAdvice = listOf(Advice(expectedStrings[0], expectedStrings[1], expectedStrings[2]))

        val result = adviceFunctions.createAdvice(categories, appContext)


        // Assert

        assertEquals(1, result.size)
        assertEquals(expectedAdvice, result)
    }


    @Test
    fun createAdvice_coldOtherLongFur() {
        // Arrange
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val categories = listOf(
            AdviceCategory.COLDOTHERLONGFUR,
        )

        // Act
        val expectedStrings = appContext.resources.getStringArray(R.array.COLDOTHERLONGFUR)
        val expectedAdvice = listOf(Advice(expectedStrings[0], expectedStrings[1], expectedStrings[2]))

        val result = adviceFunctions.createAdvice(categories, appContext)


        // Assert

        assertEquals(1, result.size)
        assertEquals(expectedAdvice, result)

    }


    /*
    tar i mot en liste av kategorier og context og returnerer liste av advice
     */
}