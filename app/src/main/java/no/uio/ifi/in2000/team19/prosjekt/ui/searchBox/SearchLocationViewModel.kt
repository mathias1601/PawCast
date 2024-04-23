package no.uio.ifi.in2000.team19.prosjekt.ui.searchBox

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import javax.inject.Inject


sealed class SearchState {
    object Hidden: SearchState()
    object Idle: SearchState()
    object Loading: SearchState()
    object NoSuggestions: SearchState()
    data class Suggestions(val suggestions: List<PlaceAutocompleteSuggestion>) : SearchState()
    object Error : SearchState()
}



@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel(){

    // TODO: move access token to somewhere private.
    val mapboxAccessToken = "pk.eyJ1IjoibWFya3VzZXYiLCJhIjoiY2x0ZWFydGZnMGQyeTJpcnQ2ZXd6ZWdjciJ9.09_6aHo-sftYJs6mTXhOyA"
    val placeAutocomplete = PlaceAutocomplete.create(mapboxAccessToken)



    private val _searchFieldValue: MutableStateFlow<String> = MutableStateFlow("initial")
    val searchFieldValue: StateFlow<String> = _searchFieldValue.asStateFlow()



    init {
        viewModelScope.launch(Dispatchers.IO) {
            _searchFieldValue.value = settingsRepository.getCords().detailedName
        }
    }

    fun updateSearchField(search:String){
        _searchFieldValue.value = search
    }

    private val _searchState: MutableStateFlow<SearchState> = MutableStateFlow(SearchState.Hidden)
    val searchState : StateFlow<SearchState> = _searchState.asStateFlow()

    private var debounceJob: Job? = null

    private var topChoice = mutableStateOf<PlaceAutocompleteSuggestion?>(null)

    fun searchLocation(query:String) {

        _searchState.value = SearchState.Loading

        debounceJob?.cancel()

        debounceJob = viewModelScope.launch (Dispatchers.IO){

            try {

                val DEBOUNCE_DELAY = 200
                delay(timeMillis = DEBOUNCE_DELAY.toLong()) // Debounce / wait 200 ms.

                // Debounce allows us not call the API for every letter typed, only when the user typed then paused for 200 ms

                if (isActive){
                    val response = placeAutocomplete.suggestions(query)

                    // ⛔ if API returns error or response is null
                    if (response.value == null || response.isError) {
                        _searchState.value = SearchState.Error
                    }

                    // ✅ if search is Successful
                    else if (response.value!!.isNotEmpty()){
                        topChoice.value = response.value!![0] // save top result in case user just presses done.
                        _searchState.value = SearchState.Suggestions(response.value!!)
                    }

                    // 👎 There was no results.
                    else {
                        _searchState.value = SearchState.NoSuggestions
                    }
                }
            } catch (e: CancellationException){
                Log.d("Search", "Debounce cancelled. New Job started.")
            }
        }
    }

    fun selectSearchLocation(selectedSuggestion: PlaceAutocompleteSuggestion){

        _searchState.value = SearchState.Hidden

        viewModelScope.launch(Dispatchers.IO) {
            val response = placeAutocomplete.select(selectedSuggestion)
            if (response.isValue){
                settingsRepository.updateCoords(
                    latitude = response.value!!.coordinate.latitude().toString(),
                    longitude = response.value!!.coordinate.longitude().toString(),
                    shortName = response.value!!.name,
                    detailedName = response.value!!.address!!.formattedAddress!!,
                )
                // TODO: Er det heeelt sikkert at det aldri er nullable?
            }

            updateSearchBoxToRepresentStoredLocation()
        }

    }

    fun updateSearchBoxToRepresentStoredLocation(){

        viewModelScope.launch (Dispatchers.IO){
            val cords = settingsRepository.getCords()
            _searchFieldValue.value = cords.shortName
        }


    }

    fun setSearchStateToIdle(){
        _searchState.value = SearchState.Idle
    }

    fun setSearchStateToHidden(){
        _searchState.value = SearchState.Hidden
    }

    // Method is ran if user just presses done on their keyboard. Then we selected the top result from the earlier search
    fun pickTopResult() {

        if (topChoice.value != null){
            selectSearchLocation(topChoice.value!!)
        }


    }
}

