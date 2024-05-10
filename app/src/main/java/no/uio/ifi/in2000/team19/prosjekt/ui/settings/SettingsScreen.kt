package no.uio.ifi.in2000.team19.prosjekt.ui.settings


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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


        Column(
            modifier = Modifier
                .padding(
                    horizontal = 50.dp,
                    vertical = 100.dp
                ) // Global horizontal padding for all settings items.
                .fillMaxSize()
            ,
        ) {
            Row ( verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = stringResource(R.string.settings))
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = stringResource(R.string.settings), style = MaterialTheme.typography.titleLarge)
            }



            CategoryDivider(text = stringResource(id = R.string.location))
            Column (
                modifier = Modifier.padding(top = 10.dp)
            ){
                SearchLocationTextField(viewModel = searchLocationViewModel)
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = stringResource(R.string.location_disclaimer), style = MaterialTheme.typography.labelMedium)
            }
            
            
            CategoryDivider(text = stringResource(R.string.your_dog_title))
            Button(onClick = { viewModel.clearDataStore() }) {
                Text(text = "Endre hunde profilen din")
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