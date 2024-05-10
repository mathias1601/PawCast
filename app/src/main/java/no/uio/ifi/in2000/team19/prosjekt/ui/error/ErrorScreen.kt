package no.uio.ifi.in2000.team19.prosjekt.ui.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.model.ErrorReasons

@Composable
fun ErrorScreen(onReload: () -> Unit, reason:ErrorReasons) {

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        ExtendedFloatingActionButton(
            text = { Text(
                when (reason) {
                    ErrorReasons.INTERNET -> stringResource(R.string.no_internet_text)
                    ErrorReasons.INTERRUPTION -> stringResource(R.string.interruption_text)
                    ErrorReasons.DATABASE -> stringResource(R.string.database_text)
                    ErrorReasons.UNKNOWN -> stringResource(R.string.unknown_error_text)
                }
            )
           },
            icon = {
                when (reason){
                    ErrorReasons.INTERNET -> Icon(imageVector = Icons.Filled.WifiOff, contentDescription = stringResource(R.string.no_wifi_icon_description))
                    ErrorReasons.INTERRUPTION -> Icon(imageVector = Icons.Filled.Close, contentDescription = stringResource(R.string.close_icon_description))
                    ErrorReasons.DATABASE -> Icon(imageVector = Icons.Filled.DataObject, contentDescription = stringResource(R.string.an_error_occured_reading_from_the_database))
                    ErrorReasons.UNKNOWN -> Icon(imageVector = Icons.Filled.QuestionMark, contentDescription = stringResource(R.string.an_unkown_error_occured))
                }
            },

            onClick = onReload
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Text(text = stringResource(R.string.press_to_try_again), style = MaterialTheme.typography.labelLarge)
    }
}