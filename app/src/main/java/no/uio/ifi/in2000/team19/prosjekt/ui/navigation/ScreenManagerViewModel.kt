package no.uio.ifi.in2000.team19.prosjekt.ui.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ScreenManagerViewModel @Inject constructor() : ViewModel(){

    private var _navBarSelectedIndex : MutableStateFlow<Int> = MutableStateFlow(0)
    var navBarSelectedIndex: StateFlow<Int> = _navBarSelectedIndex.asStateFlow()

    fun updateNavBarSelectedIndex(newIndex:Int){
        _navBarSelectedIndex.value = newIndex
    }
}