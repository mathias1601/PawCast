package no.uio.ifi.in2000.team19.prosjekt

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsDatabase
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.coordsDao
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CoordsDatabaseTest {
    private lateinit var dao: coordsDao
    private lateinit var db: SettingsDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, SettingsDatabase::class.java).build()
        dao = db.getCoordsDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun checkDatabaseContent() {
        val expectedCoords = Cords(0,"navn", "navn2", "60", "10")
        dao.insertCords(expectedCoords)

        val coords = dao.getCords()
        assertEquals(expectedCoords, coords)

        val fakeCoords = Cords(0, "hallo", "ok", "2", "5")
        dao.deleteCords(fakeCoords)

        assertEquals(expectedCoords, coords)

        dao.deleteCords(expectedCoords)

        assertEquals(null, dao.getCords())
    }
}