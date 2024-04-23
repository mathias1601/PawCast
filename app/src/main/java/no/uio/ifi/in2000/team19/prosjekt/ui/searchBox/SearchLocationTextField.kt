package no.uio.ifi.in2000.team19.prosjekt.ui.searchBox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion


@Composable
fun SearchLocationTextField(
    viewModel: SearchLocationViewModel
){
    val searchQuery = viewModel.searchFieldValue.collectAsState().value
    var isTextFieldFocused by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
    ) {

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isTextFieldFocused = focusState.isFocused
                    if (isTextFieldFocused) {
                        viewModel.setSearchStateToIdle()
                    } else {
                        viewModel.setSearchStateToHidden()
                        viewModel.updateSearchBoxToRepresentStoredLocation()
                    }
                }
            ,

            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.pickTopResult()
                    viewModel.updateSearchBoxToRepresentStoredLocation()
                }
            ),

            value = searchQuery,
            singleLine = true,
            onValueChange = { query ->
                viewModel.updateSearchField(query)
                viewModel.searchLocation(query)

            },
            label = {Text("Search for location")},
            leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Location Search-field") }
        )


        val searchState: SearchState = viewModel.searchState.collectAsState().value





        AnimatedVisibility(

            modifier = Modifier
            ,
            visible = (searchState != SearchState.Hidden)

        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(10.dp)
            ) {

            }
            when (searchState) {
                is SearchState.Loading -> Loading()
                is SearchState.NoSuggestions -> NoSuggestions()
                is SearchState.Error -> Error()
                is SearchState.Suggestions -> SearchSuggestions(searchState.suggestions, viewModel, focusManager)
                is SearchState.Idle -> Idle()
                else ->  { /* Do nothing. Should never get here. */}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Loading(){
    Column(
        Modifier.height(300.dp).background(Color.LightGray).padding(horizontal = 40.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(text = "Laster inn resultater")
    }
}


@Composable
fun Idle(){
    Column(
        Modifier.height(300.dp).background(Color.LightGray).padding(horizontal = 40.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Begynn å skrive for å se resultater")
    }
}


@Composable
fun Error(){
    Column(
        Modifier.height(300.dp).background(Color.LightGray).padding(horizontal = 40.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "En feil skjedde")
    }
}

@Composable
fun NoSuggestions(){
    Column(
        Modifier.height(300.dp).background(Color.LightGray).padding(horizontal = 40.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Ingen resultater")
    }
}



@Composable
fun SearchSuggestions(
    suggestions: List<PlaceAutocompleteSuggestion>,
    viewModel: SearchLocationViewModel,
    focusManager: FocusManager
){

    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)

    ){
        items(suggestions) { suggestion ->
            SearchSuggestion(suggestion, viewModel, focusManager)
        }
    }
}

@Composable
fun SearchSuggestion(
    suggestion: PlaceAutocompleteSuggestion,
    viewModel: SearchLocationViewModel,
    focusManager: FocusManager, ) {


    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
        ,
        onClick = {

            keyboardController?.hide()
            focusManager.clearFocus()
            viewModel.selectSearchLocation(suggestion)

        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row {

                Icon(imageVector = Icons.Filled.Place, contentDescription = "icon of Place")

                if (suggestion.formattedAddress == null){
                    Text(text = suggestion.name, maxLines = 1)
                } else {
                    Text(text = suggestion.formattedAddress!!, maxLines = 1)
                }
            }


        }
    }
}

