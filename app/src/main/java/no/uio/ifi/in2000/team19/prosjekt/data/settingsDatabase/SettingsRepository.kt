package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase

import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.coordsDao
import javax.inject.Inject


class SettingsRepository @Inject constructor(private val coordsDao: coordsDao){

    suspend fun getCords(): Cords {

        if (coordsDao.getCords() == null){
            val defaultCoords = Cords(0, "60", "10")
            coordsDao.insertCords(defaultCoords)
        }
        return coordsDao.getCords()!!
    }

    suspend fun updateCoords(latitude:String, longitude:String) {
        val cords = Cords(0, longitude, latitude) // altid id: 0 så vi kan overskrive eldre longitude/latitude
        coordsDao.insertCords(cords)
    }

    suspend fun deleteCoords(cords: Cords) {
        coordsDao.deleteCords(cords)
    }


}