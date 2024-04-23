package no.uio.ifi.in2000.team19.prosjekt.ui.settings


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationTextField
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
    searchLocationViewModel: SearchLocationViewModel
){

    val mapboxAccessToken = stringResource( id = R.string.mapbox_access_token)


    Column (

        modifier = Modifier
            .fillMaxSize()
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center


    ){


        Column(
            modifier = Modifier
                .padding(
                    horizontal = 50.dp,
                    vertical = 40.dp
                ) // Global horizontal padding for all settings items.
                .fillMaxSize()
            ,
        ) {

            CategoryDivider(text = "Location")
            Column (
                modifier = Modifier.padding(top = 10.dp)
            ){
                SearchLocationTextField(viewModel = searchLocationViewModel)
            }




            CategoryDivider(text = "Debug")
            Button(onClick = { viewModel.clearDataStore() }) {
                Text(text = "Reset to setup")
            }
        }
    }
}

@Composable
fun CategoryDivider(text: String){

    Column(
        modifier = Modifier.padding(top = 40.dp)
    ) {
        Text(text = text)
        HorizontalDivider()
    }

}