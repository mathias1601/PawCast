package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cords(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "longitude") var longitude: String,
    @ColumnInfo(name = "latitude") var latitude: String,
)