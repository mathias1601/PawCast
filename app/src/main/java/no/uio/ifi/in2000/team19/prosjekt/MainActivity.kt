package no.uio.ifi.in2000.team19.prosjekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import no.uio.ifi.in2000.team19.prosjekt.examples.MapBoxSimpleExample
import no.uio.ifi.in2000.team19.prosjekt.ui.HomeScreenManager
import no.uio.ifi.in2000.team19.prosjekt.ui.theme.Team19prosjektoppgaveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team19prosjektoppgaveTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MapBoxSimpleExample()
                    HomeScreenManager()
                }
            }
        }
    }
}
