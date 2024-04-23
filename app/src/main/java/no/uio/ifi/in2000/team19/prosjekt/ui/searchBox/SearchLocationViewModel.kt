package no.uio.ifi.in2000.team19.prosjekt.ui.searchBox

import androidx.lifecycle.ViewModel
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import javax.inject.Inject


sealed class SearchState {
    object Idle: SearchState()
    object Loading: SearchState()
    data class Suggestions(val suggestions: List<PlaceAutocompleteSuggestion>) : SearchState()
    data class Selected(val suggestion : PlaceAutocompleteSuggestion) : SearchState()
    object Error : SearchState()
}



@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel(){

    private val _searchingQuery:MutableStateFlow<String> = MutableStateFlow("")
    val searchingQuery:StateFlow<String> = _searchingQuery.asStateFlow()



    fun onSearchQueryChanged(query:String){

    }
}

