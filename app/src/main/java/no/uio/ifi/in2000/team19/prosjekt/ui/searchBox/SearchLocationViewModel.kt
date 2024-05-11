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
import javax.inject.Named


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
    private val settingsRepository: SettingsRepository,
    @Named("mapboxAccessToken") private val mapboxAccessToken: String
) : ViewModel(){



    private val _isDone: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isDone : StateFlow<Boolean> = _isDone.asStateFlow()



    // TODO: Access token should be handled better. Ask veileder
    private val placeAutocomplete = PlaceAutocomplete.create(mapboxAccessToken)

    private val _searchFieldValue: MutableStateFlow<String> = MutableStateFlow("initial")
    val searchFieldValue: StateFlow<String> = _searchFieldValue.asStateFlow()


    // Set Text in TextField to match stored value
    init {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.getCords().collect {
                _searchFieldValue.value = it.detailedName

                if (it.detailedName != ""){ // if database is already populated from database.
                    _isDone.value = true
                }

            }
        }
    }

    fun updateSearchField(search:String){
        _searchFieldValue.value = search
    }


    private val _searchState: MutableStateFlow<SearchState> = MutableStateFlow(SearchState.Hidden)
    val searchState : StateFlow<SearchState> = _searchState.asStateFlow()

    private var debounceJob: Job? = null
    private var topSuggestion = mutableStateOf<PlaceAutocompleteSuggestion?>(null)

    // Takes Query from TextBox.
    // 🚨 NB: Is called everytime the user makes a change to the text field. Therefore needs debounceing
    fun searchLocation(query:String) {

        _searchState.value = SearchState.Loading

         // Debounce is about waiting 200ms to make sure the user has stopped typing. Helps with making less API calls.
        debounceJob?.cancel() // Cancel last job (if it exists)
        val DEBOUNCE_DELAY = 200 // milliseconds


        // Assigning this coroutine to a variable allows us to use methods like .cancel()
        debounceJob = viewModelScope.launch (Dispatchers.IO){

            try {

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
                        topSuggestion.value = response.value!![0] // Save top result in case user just presses done.
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

    // Tell API that we have selected this suggestions. API then returns more detailed info about Place.
    fun selectSearchLocation(selectedSuggestion: PlaceAutocompleteSuggestion){

        _searchState.value = SearchState.Hidden

        viewModelScope.launch(Dispatchers.IO) {
            val response = placeAutocomplete.select(selectedSuggestion)
            if (response.isValue){
                settingsRepository.updateCoords(
                    latitude = response.value!!.coordinate.latitude().toString(),
                    longitude = response.value!!.coordinate.longitude().toString(),
                    shortName = response.value!!.name,
                    detailedName = response.value!!.address!!.formattedAddress ?: response.value!!.name, // some adresses dont have a detailedName.
                )

                updateSearchBoxToRepresentStoredLocation()
                _isDone.value = true

            } else {
                _searchState.value = SearchState.Error
            }

        }

    }

    fun updateSearchBoxToRepresentStoredLocation(){

        viewModelScope.launch (Dispatchers.IO){
            val cords = settingsRepository.getCords()
             cords.collect {
                 _searchFieldValue.value = it.shortName
            }
        }
    }

    fun setSearchStateToIdle(){
        _isDone.value = false
        _searchState.value = SearchState.Idle
    }

    fun setSearchStateToHidden(){
        _searchState.value = SearchState.Hidden
    }

    fun pickTopResult() {

        if (topSuggestion.value != null){
            selectSearchLocation(topSuggestion.value!!)
        }


    }
}

