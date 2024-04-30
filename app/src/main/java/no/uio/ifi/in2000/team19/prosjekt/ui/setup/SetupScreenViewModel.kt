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
import no.uio.ifi.in2000.team19.prosjekt.data.dataStore.DataStoreRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class SetupScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {

    private var _userInfo: MutableStateFlow<UserInfo> = MutableStateFlow(UserInfo(0,"undefined", "undefined", false,false,false, false, false, false, false, false, false, false))
    var userInfo: StateFlow<UserInfo> = _userInfo.asStateFlow()

    fun updateUserName(userName: String){
        val updatedUserName = _userInfo.value.copy(userName = userName)
        _userInfo.value = updatedUserName
        Log.d("SETUP_DEBUG", _userInfo.value.userName)

    }
    fun updateDogName(dogName: String){
        val updatedDogName = _userInfo.value.copy(dogName = dogName)
        _userInfo.value = updatedDogName
        Log.d("SETUP_DEBUG", _userInfo.value.dogName)
    }
    fun updateAge(newValue: Boolean) {
        //TODO add puppy here
        _userInfo.value.isSenior = newValue
        Log.d("SETUP_DEBUG", _userInfo.value.isSenior.toString())
    }
    fun updateNose(newValue: Boolean){
        _userInfo.value.isFlatNosed = newValue
        Log.d("SETUP_DEBUG", _userInfo.value.isFlatNosed.toString())
    }
    fun updateThin(newValue: Boolean) {
        _userInfo.value.isThin = newValue
        Log.d("SETUP_DEBUG", _userInfo.value.isThin.toString())
    }

    fun updateFilterCategories(categoryName: String, newValue: Boolean) {
        when (categoryName) {
            "tynnPels" -> { _userInfo.value.isThinHaired = newValue }
            "tykkPels" -> {_userInfo.value.isThickHaired = newValue}
            "langPels" -> {_userInfo.value.isLongHaired = newValue}
            "kortPels" -> {_userInfo.value.isShortHaired = newValue}
            "lysPels" -> {_userInfo.value.isLightHaired = newValue}
            "moerkPels" -> {_userInfo.value.isDarkHaired = newValue}
        }
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

    fun saveSetupState(isCompleted:Boolean){
        viewModelScope.launch (Dispatchers.IO){
            dataStoreRepository.saveSetupState(isCompleted)
        }
    }
}