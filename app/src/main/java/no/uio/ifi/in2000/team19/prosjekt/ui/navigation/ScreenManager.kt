package no.uio.ifi.in2000.team19.prosjekt.ui.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team19.prosjekt.ui.extendedAdvice.AdviceScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenManager
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupManager
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherScreenViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenManager(
    viewModel: ScreenManagerViewModel,
) {

    val navController = rememberNavController()
    val navBarItems = createBottomNavbarItems()

    val navBarSelectedItemIndex = viewModel.navBarSelectedIndex.collectAsState().value
    val startDestination = viewModel.startDestination.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value


    Scaffold(

        bottomBar = {
            if (startDestination == "home" || startDestination == "weather" || startDestination == "settings")  {
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
                                    imageVector = if (index == navBarSelectedItemIndex) {
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
        }
    ) {innerPadding ->

        Column(
        ) {
                //Sjekk kun for når man åpner appen
            NavHost(
                navController = navController,
                startDestination = "parent",

            ){

                navigation(
                    startDestination = startDestination,
                    route = "parent"
                ){

                    composable("home") { backStackEntry ->

                        // hiltViewModel creates new viewmodel model if there is none and stores it scoped to the navigation graph. https://developer.android.com/reference/kotlin/androidx/hilt/navigation/compose/package-summary
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("parent") }
                        val homeScreenViewModel: HomeScreenViewModel = hiltViewModel(parentEntry)

                        HomeScreenManager(
                            viewModel = homeScreenViewModel,
                            navController = navController
                        )

                    }


                    composable("settings"){ backStackEntry ->

                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("parent") }

                        val settingsScreenViewModel: SettingsScreenViewModel = hiltViewModel(parentEntry)
                        val searchLocationViewModel: SearchLocationViewModel = hiltViewModel(parentEntry)

                        SettingsScreen(
                            viewModel = settingsScreenViewModel,
                            searchLocationViewModel = searchLocationViewModel
                        )
                    }

                    composable("weather"){backStackEntry ->

                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("parent") }
                        val weatherScreenViewModel: WeatherScreenViewModel = hiltViewModel(parentEntry)

                        WeatherScreen(
                            weatherScreenViewModel = weatherScreenViewModel,
                            navController = navController,
                            innerPadding = innerPadding,

                        )
                    }

                    composable("setup/{STAGE}"){ backStackEntry ->

                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("parent") }
                        val setupScreenViewModel: SetupScreenViewModel = hiltViewModel(parentEntry)
                        val searchLocationViewModel : SearchLocationViewModel = hiltViewModel(parentEntry)

                        val id = backStackEntry.arguments?.getString("STAGE") ?: "0" // 0 to force it to start if wrong parameter is supplied. Elvis operator needs to stay for when start destination supplies the argument
                        SetupManager(
                            id=id,
                            navController = navController,
                            viewModel = setupScreenViewModel,
                            searchLocationViewModel = searchLocationViewModel
                        )
                    }

                    composable("advice/{id}") {backStackEntry->

                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("parent") }
                        val homeScreenViewModel: HomeScreenViewModel = hiltViewModel(parentEntry)

                        val id = backStackEntry.arguments?.getString("id") ?: "0"

                        AdviceScreen(
                            navController = navController,
                            adviceId = id.toInt(),
                            viewModel = homeScreenViewModel)
                    }
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