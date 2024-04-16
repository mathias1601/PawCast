package no.uio.ifi.in2000.team19.prosjekt.ui.setup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SetupScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private var _userInfo: MutableStateFlow<UserInfo> = MutableStateFlow(UserInfo(0,"undefined", "undefined", false,false,false))
    var userInfo: StateFlow<UserInfo> = _userInfo.asStateFlow()

    fun updateUserName(userName: String){
        _userInfo.value.userName = userName
        Log.d("SETUP_DEBUG", userName)

    }
    fun updateDogName(dogName: String){
        _userInfo.value.dogName = dogName
        Log.d("SETUP_DEBUG", dogName)
    }
    fun updateSize(size: Boolean) {
        _userInfo.value.isBig = size
    }
    fun updateNose(nose: Boolean){
        _userInfo.value.isShortNosed = nose
    }
    fun updateHair(hair: Boolean){
        _userInfo.value.isLongHaired = hair
    }
    fun saveUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                settingsRepository.updateUserInfo(_userInfo.value)
                Log.d("SETUP_DEBUG", _userInfo.value.toString())
            }  catch (e: IOException) {
                println(e)
                Log.d("SETUP_DEBUG", e.toString())
            }
        }
    }
}