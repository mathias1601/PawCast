package no.uio.ifi.in2000.team19.prosjekt.ui.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsDatabase
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository


class ScreenManagerViewModel(application: Application) : AndroidViewModel(application){

    private val repository: SettingsRepository
    private var _navBarSelectedIndex : MutableStateFlow<Int> = MutableStateFlow(0)
    var navBarSelectedIndex: StateFlow<Int> = _navBarSelectedIndex.asStateFlow()

    init {
        val coordsDao = SettingsDatabase.getDatabase(application).coordsDao()
        repository = SettingsRepository(coordsDao)
    }

    fun getSettingsRepository(): SettingsRepository {
        return repository
    }

    fun updateNavBarSelectedIndex(newIndex:Int){
        _navBarSelectedIndex.value = newIndex
    }
}