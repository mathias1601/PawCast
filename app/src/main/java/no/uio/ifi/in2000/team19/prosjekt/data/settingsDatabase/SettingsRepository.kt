package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase

import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.coordsDao


class SettingsRepository(private val coordsDao: coordsDao){

    suspend fun getCoords(): Cords {

        if (coordsDao.getCords() == null){
            val defaultCoords = Cords(0, "61", "9")
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