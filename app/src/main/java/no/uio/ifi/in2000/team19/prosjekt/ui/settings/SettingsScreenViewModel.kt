package no.uio.ifi.in2000.team19.prosjekt.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.dataStore.DataStoreRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _cordsUiState: MutableStateFlow<Cords> = MutableStateFlow(Cords(0, "12", "34"))
    val cordsUiState: StateFlow<Cords> = _cordsUiState.asStateFlow()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            _cordsUiState.value = settingsRepository.getCords()

        }
    }

    fun setCoordinates(newLatitude: String, newLongitude: String) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateCoords(newLatitude, newLongitude)
            _cordsUiState.value = settingsRepository.getCords()
        }
    }


    fun clearDataStore(){
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.clearDataStore()
        }
    }

    ////////////////// MAPBOX SEARCH ///////////////////////////////////

    private val searchEngine: SearchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
        SearchEngineSettings("pk.eyJ1IjoibWFya3VzZXYiLCJhIjoiY2x0ZWFydGZnMGQyeTJpcnQ2ZXd6ZWdjciJ9.09_6aHo-sftYJs6mTXhOyA")
    )
    private var searchRequestTask: AsyncOperationTask? = null

    fun searchForLocation(query: String, onResult: (result: List<SearchResult>) -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            searchRequestTask?.cancel()
            searchRequestTask =
                searchEngine.search(query, SearchOptions(limit = 5), object :
                    SearchSelectionCallback {
                    override fun onSuggestions(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
                        if (suggestions.isNotEmpty()) {
                            // In real application, you might want to show these suggestions to the user.
                            searchRequestTask = searchEngine.select(suggestions.first(), this)
                        }
                    }

                    override fun onResult(suggestion: SearchSuggestion, result: SearchResult, responseInfo: ResponseInfo) {
                        onResult(listOf(result))
                    }

                    override fun onResults(suggestion: SearchSuggestion, results: List<SearchResult>, responseInfo: ResponseInfo) {
                        onResult(results)
                    }

                    override fun onError(e: Exception) {
                        onError(e)
                    }
                })
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchRequestTask?.cancel()
    }
}