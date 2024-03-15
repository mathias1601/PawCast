package no.uio.ifi.in2000.team19.prosjekt.examples.RoomDataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cords(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "longitude") val longitude: String?,
    @ColumnInfo(name = "latitude")val latitude: String?,
)