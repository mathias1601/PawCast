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



    // COMMON
    private var _userInfo: MutableStateFlow<UserInfo> = MutableStateFlow(UserInfo(0,"undefined", "undefined", false,false,false, false, false, false, false, false, false))
    var userInfo: StateFlow<UserInfo> = _userInfo.asStateFlow()



    // SCREEN ONE
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



    // SCREEN TWO

    private var _selectedAgeIndex: MutableStateFlow<Int?> = MutableStateFlow(null) // Null when none are chosen.
    var selectedAgeIndex: StateFlow<Int?> = _selectedAgeIndex.asStateFlow()

    fun updateAgeIndex(newIndex:Int){
        _selectedAgeIndex.value = newIndex
    }
    fun updateIsSenior(newValue: Boolean) {
        _userInfo.value.isSenior = newValue
        Log.d("SETUP_DEBUG", _userInfo.value.isSenior.toString())
    }




    // SCREEN THREE

    private var _selectedNoseIndex: MutableStateFlow<Int?> = MutableStateFlow(null) // Null when none are chosen.
    var selectedNoseIndex: StateFlow<Int?> = _selectedNoseIndex.asStateFlow()

    fun updateIsFlatNosed(newValue: Boolean){
        _userInfo.value.isFlatNosed = newValue
        Log.d("SETUP_DEBUG", _userInfo.value.isFlatNosed.toString())
    }

    fun updateNoseIndex(newIndex:Int){
        _selectedNoseIndex.value = newIndex
    }




    // SCREEN FOUR

    private var _selectedThinIndex: MutableStateFlow<Int?> = MutableStateFlow(null)
    var selectedThinIndex:StateFlow<Int?> = _selectedThinIndex.asStateFlow()

    fun updateIsThin(newValue: Boolean) {
        _userInfo.value.isThin = newValue
        Log.d("SETUP_DEBUG", _userInfo.value.isThin.toString())
    }

    fun updateThinIndex(newIndex: Int){
        _selectedThinIndex.value = newIndex
    }






    // SCREEN FIVE
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



    // SAVE EVERY CHOICE. Only done at the end to make sure the user finnished the Setup process.
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