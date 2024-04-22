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

sealed class SetupState {
    data object Loading: SetupState()
    data object Success : SetupState()
    data object Error: SetupState()

    data object SuccessButIsNull: SetupState()
}

@HiltViewModel
class ScreenManagerViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel(){

    private var _navBarSelectedIndex : MutableStateFlow<Int> = MutableStateFlow(0)
    var navBarSelectedIndex: StateFlow<Int> = _navBarSelectedIndex.asStateFlow()

    private var _userInfo: MutableStateFlow<SetupState> = MutableStateFlow(SetupState.Loading)
    var userInfo: StateFlow<SetupState> = _userInfo.asStateFlow()

    fun updateNavBarSelectedIndex(newIndex:Int){
        _navBarSelectedIndex.value = newIndex
    }
    fun initialize() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userData = settingsRepository.getUserInfo()
                if (userData == null) {
                    _userInfo.value = SetupState.SuccessButIsNull
                } else {
                    _userInfo.value = SetupState.Success
                }
            } catch (e: Exception) {
                _userInfo.value = SetupState.Error
            }

        }
    }

}