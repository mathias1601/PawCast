package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.MutableStateFlow

@Entity
data class UserInfo(
    @PrimaryKey(autoGenerate = false) val id: Int,
    //UserInfo
    @ColumnInfo(name = "userName") var userName: String,
    @ColumnInfo(name = "dogName") var dogName: String,
    //Age
    @ColumnInfo(name = "isSenior") var isSenior: Boolean,
    @ColumnInfo(name = "isPuppy") var isPuppy: Boolean,

    //Nose
    @ColumnInfo(name = "isFlatNosed") var isFlatNosed: Boolean,
    //Body
    @ColumnInfo(name = "isThin") var isThin: Boolean,
    //Fur
    @ColumnInfo(name = "isLongHaired") var isLongHaired: Boolean,
    @ColumnInfo(name = "isShortHaired") var isShortHaired: Boolean,

    @ColumnInfo(name = "isThinHaired") var isThinHaired: Boolean,
    @ColumnInfo(name = "isThickhaired") var isThickHaired: Boolean,

    @ColumnInfo(name = "isLightHaired") var isLightHaired: Boolean,
    @ColumnInfo(name = "isDarkHaired") var isDarkHaired: Boolean,



)
