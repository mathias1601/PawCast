package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.coordsDao


@Database(
    entities = [Cords::class],
    version = 1
)
abstract class SettingsDatabase: RoomDatabase() {
    abstract fun coordsDao(): coordsDao

    companion object{
        @Volatile
        private var INSTANCE: SettingsDatabase? = null

        fun getDatabase(context: Context) : SettingsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SettingsDatabase::class.java,
                    "settingsDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}