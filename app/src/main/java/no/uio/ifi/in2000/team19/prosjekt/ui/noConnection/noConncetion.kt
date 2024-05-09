package no.uio.ifi.in2000.team19.prosjekt.ui.noConnection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NoConnectionScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ExtendedFloatingActionButton(
            text = { Text("Ingen internett-tilgang") },
            icon = { Icon(Icons.Filled.WifiOff, contentDescription = "Advarsel") },
            onClick = { /* TODO change later if we want to update */ }
        )
    }
}