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
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team19.prosjekt.model.ErrorReasons

@Composable
fun ErrorScreen(onReload: () -> Unit, reason:ErrorReasons) {

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        ExtendedFloatingActionButton(
            text = { Text(
                when (reason) {
                    ErrorReasons.INTERNET -> "Ingen internett tilgang"
                    ErrorReasons.INTERRUPTION -> "Noe gikk galt under henting av vær"
                    ErrorReasons.DATABASE -> "Henting lokal-lagret gikk galt"
                    ErrorReasons.UNKNOWN -> "En ukjent feil oppstod."
                }
            )
           },
            icon = {
                when (reason){
                    ErrorReasons.INTERNET -> Icon(imageVector = Icons.Filled.WifiOff, contentDescription = "No wifi")
                    ErrorReasons.INTERRUPTION -> Icon(imageVector = Icons.Filled.Close, contentDescription = "An interruption occured")
                    ErrorReasons.DATABASE -> Icon(imageVector = Icons.Filled.DataObject, contentDescription = "An error occured reading from the database")
                    ErrorReasons.UNKNOWN -> Icon(imageVector = Icons.Filled.QuestionMark, contentDescription = "An unkown error occured")
                }
            },

            onClick = onReload
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Text(text = "Trykk på knappen for å prøve igjen", style = MaterialTheme.typography.labelLarge)
    }
}