package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "userName") var userName: String,
    @ColumnInfo(name = "dogName") var dogName: String,
    @ColumnInfo(name = "dogTypes") var dogTypes: String,
)
