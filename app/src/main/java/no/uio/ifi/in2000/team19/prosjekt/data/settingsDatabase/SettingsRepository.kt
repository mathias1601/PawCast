package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase

import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.coordsDao
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.userInfoDao
import javax.inject.Inject


class SettingsRepository @Inject constructor(
    private val coordsDao: coordsDao,
    private val userInfoDao: userInfoDao){

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

    suspend fun getUserInfo(): UserInfo? {
        return userInfoDao.getUserInfo()
    }

    suspend fun updateUserInfo(userInfo: UserInfo) {
        userInfoDao.insertUserInfo(userInfo)
    }

    suspend fun deleteUserInfo(userInfo: UserInfo) {
        userInfoDao.deleteUserInfo(userInfo)
    }


}