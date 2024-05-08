package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.coordsDao
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.userInfoDao
import javax.inject.Inject


class SettingsRepository @Inject constructor(
    private val coordsDao: coordsDao,
    private val userInfoDao: userInfoDao){


    // CORDS
    suspend fun getCords(): Flow<Cords> {
        val currentCords = coordsDao.getCords()?.firstOrNull()

        if (currentCords == null){
            val defaultCoords =
                Cords(
                    id = 0,
                    longitude = "60",
                    latitude = "10",
                    shortName = "Default Location",
                    detailedName = "Default Location"
                )
            coordsDao.insertCords(defaultCoords)
        }
        return coordsDao.getCords()!!
    }

    suspend fun updateCoords(latitude:String, longitude:String, shortName:String, detailedName:String) {
        val cords = Cords(
            id = 0,
            longitude = longitude,
            latitude = latitude,
            shortName = shortName,
            detailedName = detailedName,
        )
        coordsDao.insertCords(cords)
    }

    suspend fun deleteCoords(cords: Cords) {
        coordsDao.deleteCords(cords)
    }


    // USER INFO
    suspend fun getUserInfo(): UserInfo {
        if(userInfoDao.getUserInfo() == null) {
            updateUserInfo(
                UserInfo(0, "", "", false, false, false, false, false, false, false, false, false, false))
        }
        return userInfoDao.getUserInfo()!!
    }

    suspend fun updateUserInfo(userInfo: UserInfo) {
        userInfoDao.insertUserInfo(userInfo)
    }

    suspend fun deleteUserInfo(userInfo: UserInfo) {
        userInfoDao.deleteUserInfo(userInfo)
    }


}