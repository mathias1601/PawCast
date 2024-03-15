package no.uio.ifi.in2000.team19.prosjekt.examples.RoomDataBase

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [Cords::class],
    version = 1
)
abstract class CordsDatabase: RoomDatabase() {

    abstract fun dao(): DataAccessObjectDao
}