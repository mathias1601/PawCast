package no.uio.ifi.in2000.team19.prosjekt

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.navigation.ScreenManager
import no.uio.ifi.in2000.team19.prosjekt.ui.navigation.ScreenManagerViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.theme.Team19prosjektoppgaveTheme
import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherScreenViewModel

//import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherScreenViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsScreenViewModel: SettingsScreenViewModel by viewModels()
    private val screenManagerViewModel: ScreenManagerViewModel by viewModels()
    private val homeScreenViewModel: HomeScreenViewModel by viewModels()
    private val weatherScreenViewModel: WeatherScreenViewModel by viewModels()
    private val setupScreenViewModel: SetupScreenViewModel by viewModels()
    private val searchLocationViewModel : SearchLocationViewModel by viewModels()




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
                    ScreenManager(
                        viewModel = screenManagerViewModel, // viewmodel for navbar and Scaffold.
                        settingsScreenViewModel = settingsScreenViewModel,
                        homeScreenViewModel= homeScreenViewModel,
                        weatherScreenViewModel = weatherScreenViewModel,
                        setupScreenViewModel=setupScreenViewModel,
                        searchLocationViewModel = searchLocationViewModel
                        )
                }
            }
        }
    }
}
