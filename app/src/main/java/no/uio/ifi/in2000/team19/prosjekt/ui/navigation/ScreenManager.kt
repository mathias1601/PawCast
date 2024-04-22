package no.uio.ifi.in2000.team19.prosjekt.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenManager
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupManager
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherScreenViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenManager(
    settingsScreenViewModel: SettingsScreenViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    viewModel: ScreenManagerViewModel,
    weatherScreenViewModel: WeatherScreenViewModel,
    setupScreenViewModel: SetupScreenViewModel
) {

    val navController = rememberNavController()

    val userInfoUiState = viewModel.userInfo.collectAsState().value //Henter alltid null????

    val navBarItems = createBottomNavbarItems()
    val navBarSelectedItemIndex = viewModel.navBarSelectedIndex.collectAsState().value

    viewModel.initialize()

    Scaffold(
        bottomBar = {

                NavigationBar {
                    navBarItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = (index == navBarSelectedItemIndex),
                            onClick = {
                                viewModel.updateNavBarSelectedIndex(index)
                                navController.navigate(item.title)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (index == navBarSelectedItemIndex){
                                        item.selectedIcon
                                    } else {
                                        item.unselectedIcon
                                    },
                                    contentDescription = item.title
                                )
                            }
                        )
                    }
                }

        }
    ) {innerPadding ->

        Column(
            Modifier.padding(innerPadding)
        ) {

            //Sjekk kun for når man åpner appen
            NavHost(navController = navController, startDestination = "home"){
                composable("home") {

                    viewModel.initialize()
                    when (userInfoUiState) {
                        is SetupState.Loading -> Text(text = "laster...")
                        is SetupState.SuccessButIsNull -> navController.navigate("setup/0")
                        is SetupState.Success -> HomeScreenManager(viewModel = homeScreenViewModel)
                        is SetupState.Error -> Text(text = "error...")
                    }

                }

                composable("settings"){
                    SettingsScreen(settingsScreenViewModel)
                }

                composable("weather"){
                    WeatherScreen(weatherScreenViewModel)
                }
                composable("setup/{stage}"){backStackEntry ->
                    val id = backStackEntry.arguments?.getString("stage")!!
                    SetupManager(viewModel = setupScreenViewModel, id=id, navController=navController)
                }
            }


        }
    }
}


data class BottomNavBarItem (
    val title : String,
    val selectedIcon : ImageVector,
    val unselectedIcon : ImageVector,
)

fun createBottomNavbarItems() : List<BottomNavBarItem> {


    return listOf(

        BottomNavBarItem(
            title = "home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),

        BottomNavBarItem(
            title = "weather",
            selectedIcon = Icons.Filled.Cloud,
            unselectedIcon = Icons.Outlined.Cloud
        ),

        BottomNavBarItem(
            title = "settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        ),


    )
}