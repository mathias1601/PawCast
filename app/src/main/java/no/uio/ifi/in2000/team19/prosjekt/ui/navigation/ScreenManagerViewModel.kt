package no.uio.ifi.in2000.team19.prosjekt.ui.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.dataStore.DataStoreRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import javax.inject.Inject

sealed class SetupState {
    data object Loading: SetupState()
    data object Success : SetupState()
    data object Error: SetupState()

    data object SuccessButIsNull: SetupState()
}

@HiltViewModel
class ScreenManagerViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel(){

    private var _navBarSelectedIndex : MutableStateFlow<Int> = MutableStateFlow(0)
    var navBarSelectedIndex: StateFlow<Int> = _navBarSelectedIndex.asStateFlow()

    fun updateNavBarSelectedIndex(newIndex:Int){
        _navBarSelectedIndex.value = newIndex
    }

    private val _startDestination : MutableStateFlow<String> = MutableStateFlow("home")
    val startDestination : StateFlow<String> = _startDestination.asStateFlow()

    private val _isLoading:MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading : StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreRepository.readSetupState().collect() { completed ->

                if (completed){
                    _startDestination.value = "home"
                } else {
                    Log.d("TAG", "Setup is not complected. Setting start dest. ")
                    _startDestination.value = "setup/0"
                }

            }
        }
        _isLoading.value = false // stop saying loading
    }

}