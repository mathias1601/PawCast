package no.uio.ifi.in2000.team19.prosjekt

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import no.uio.ifi.in2000.team19.prosjekt.ui.navigation.ScreenManager
import no.uio.ifi.in2000.team19.prosjekt.ui.theme.Team19prosjektoppgaveTheme
import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Team19prosjektoppgaveTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    //ScreenManager()
                    WeatherScreen()
                }
            }
        }
    }
}
