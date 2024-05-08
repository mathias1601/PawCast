package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface coordsDao {

    @Upsert
    fun insertCords(cords: Cords)

    @Delete
    fun deleteCords(cords: Cords)

    @Query("SELECT * FROM cords")
    fun getCords(): Flow<Cords>?
}