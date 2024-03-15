package no.uio.ifi.in2000.team19.prosjekt.examples.RoomDataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface DataAccessObjectDao {

    @Upsert
    fun insertCords(cords: Cords)

    @Delete
    fun deleteCords(cords: Cords)

    @Query("SELECT * FROM cords")
    fun getCords(): List<Cords>
}