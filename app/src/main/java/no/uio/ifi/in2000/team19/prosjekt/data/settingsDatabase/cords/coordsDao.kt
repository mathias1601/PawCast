package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface coordsDao {

    @Upsert
    fun insertCords(cords: Cords)

    @Delete
    fun deleteCords(cords: Cords)

    @Query("SELECT * FROM cords WHERE id=0")
    fun getCords(): Cords?
}