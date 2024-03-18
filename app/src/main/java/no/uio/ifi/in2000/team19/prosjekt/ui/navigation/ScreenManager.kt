package no.uio.ifi.in2000.team19.prosjekt.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenManager
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreenViewModel

@Composable
fun ScreenManager() {


    val viewModel:ScreenManagerViewModel = viewModel()
    val navBarItems = createBottomNavbarItems()
    val navBarSelectedItemIndex = viewModel.navBarSelectedIndex.collectAsState().value
    val navController = rememberNavController()



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

                         })
                }
            }
        }
    ) {innerPadding ->

        Column(
            Modifier.padding(innerPadding)
        ) {
            NavHost(navController = navController, startDestination = "home"){
                composable("home") {
                    val homeScreenViewModel:HomeScreenViewModel = viewModel()
                    HomeScreenManager(homeScreenViewModel) }

                composable("settings"){
                    val settingsScreenViewModel : SettingsScreenViewModel = viewModel()
                    SettingsScreen(settingsScreenViewModel)
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

    // en bottom navbar burde egentlig ha 3 items, men lager den med tanke på senere utvidelse.

    return listOf(

        BottomNavBarItem(
            title = "home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),

        BottomNavBarItem(
            title = "settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )
}