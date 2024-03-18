package no.uio.ifi.in2000.team19.prosjekt.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsScreenViewModel : ViewModel() {

    private val _latitude = MutableStateFlow("60")
    private val _longitude = MutableStateFlow("10")

    val latitude = _latitude.asStateFlow()
    val longitude = _longitude.asStateFlow()

    /* TODO: change set functions to call to repository and update database
        - Currently reset to default values of 60 and 10 when user returns to screen. Easier to fix when implementing a database, imo.

    *
    */
    
    fun setLatitude(newLatitude:String){
        _latitude.value = newLatitude
    }

    fun setLongitude(newLongitude:String) {
        _longitude.value = newLongitude
    }
}