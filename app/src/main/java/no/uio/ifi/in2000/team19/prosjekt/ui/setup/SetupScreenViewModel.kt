package no.uio.ifi.in2000.team19.prosjekt.ui.setup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class SetupScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private var _setupIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    var setupIndex: StateFlow<Int> = _setupIndex.asStateFlow()

}