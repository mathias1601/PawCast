package no.uio.ifi.in2000.team19.prosjekt.ui.navigation

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
import javax.inject.Inject

@HiltViewModel
class ScreenManagerViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel(){

    private var _navBarSelectedIndex : MutableStateFlow<Int> = MutableStateFlow(0)
    var navBarSelectedIndex: StateFlow<Int> = _navBarSelectedIndex.asStateFlow()

    private var _userInfoUiState:MutableStateFlow<UserInfo?> = MutableStateFlow(null) //Blir ikke oppdatert :((
    var userInfoUiState: StateFlow<UserInfo?> = _userInfoUiState.asStateFlow()

    fun updateNavBarSelectedIndex(newIndex:Int){
        _navBarSelectedIndex.value = newIndex
    }
    fun initialize() {
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = settingsRepository.getUserInfo()
            _userInfoUiState.value = userInfo
        }
    }
    fun checkifDbIsNull(): Boolean {
        initialize()
        return userInfoUiState.value == null
    }
}