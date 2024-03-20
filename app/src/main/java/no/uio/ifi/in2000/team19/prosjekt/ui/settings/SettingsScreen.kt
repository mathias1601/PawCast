package no.uio.ifi.in2000.team19.prosjekt.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsScreenViewModel){

    val keyboardController = LocalSoftwareKeyboardController.current
    val cordinates = viewModel.coordinates.collectAsState().value

    var localLatitude by rememberSaveable { mutableStateOf(cordinates.latitude) }
    var localLongitude by rememberSaveable { mutableStateOf(cordinates.longitude) }

    Column (

        modifier = Modifier
            .fillMaxSize()
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center


    ){

        OutlinedTextField(
            label = { Text(text = "Latitude") },
            value = localLatitude,
            onValueChange = {localLatitude = it},

            singleLine = true,

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),

            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    viewModel.setLatitude(localLatitude)
                }
            )
        )

        OutlinedTextField(
            label = { Text(text = "Longitude") },
            value = localLongitude,
            onValueChange = {localLongitude = it},

            singleLine = true,

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    viewModel.setLongitude(localLongitude)
                }
            )
        )
    }



}
