package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.coordsDao
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.userInfoDao


@Database(
    entities = [Cords::class, UserInfo::class],
    version = 1
)
abstract class SettingsDatabase: RoomDatabase() {
    abstract fun getCoordsDao(): coordsDao
    abstract fun getUserInfoDao(): userInfoDao


}