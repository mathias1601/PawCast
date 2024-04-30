package no.uio.ifi.in2000.team19.prosjekt

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenManager
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupManager
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupScreenViewModel
//import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherScreen
//import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherScreenViewModel

/*
sealed class Screen(val route: String) {
    object HomeScreen: Screen("home")
    object WeatherScreen: Screen("weather")
    object SettingsScreen: Screen("settings")
    object SetupScreen: Screen("setup/{stage}")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    userInfoUiState: UserInfo?,
    settingsScreenViewModel: SettingsScreenViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    weatherScreenViewModel: WeatherScreenViewModel,
    setupScreenViewModel: SetupScreenViewModel
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {

            composable(Screen.HomeScreen.route) {
                if (userInfoUiState == null) {
                    navController.navigate("setup/0")
                } else {
                    HomeScreenManager(homeScreenViewModel)
                }
            }

            composable(Screen.SettingsScreen.route){
                SettingsScreen(settingsScreenViewModel)
            }

            composable(Screen.WeatherScreen.route){
                WeatherScreen(weatherScreenViewModel)
            }
            composable(Screen.SetupScreen.route){backStackEntry ->
                val id = backStackEntry.arguments?.getString("stage")!!
                SetupManager(viewModel = setupScreenViewModel, id=id, navController=navController)
            }

    }

    /*
    *
    * legger til composables når vi har flere screens enn home
    *
    */
}

*/

