package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords

@Dao
interface userInfoDao {

    @Upsert //istedenfor insert (kilde: markus)
    fun insertUserInfo(userInfo: UserInfo)

    @Delete
    fun deleteUserInfo(userInfo: UserInfo)

    @Query("SELECT * FROM userInfo")
    fun getUserInfo(): UserInfo?
}