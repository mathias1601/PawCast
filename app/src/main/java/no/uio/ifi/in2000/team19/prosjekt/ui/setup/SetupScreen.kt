package no.uio.ifi.in2000.team19.prosjekt.ui.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.AgeSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.BodySetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.FurSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.LocationSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.NamesSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.NoseSetupScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupManager(
    viewModel: SetupScreenViewModel,
    searchLocationViewModel: SearchLocationViewModel,
    id: String,
    navController: NavHostController
) {

    viewModel.initialize()

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "${id.toInt()+1} / 6",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    if (id != "0"){
                        IconButton(onClick = {
                            // This double if check needs to stay as there is some bug in "hiding" the navigation Icon that does hide the icon,
                            // but allows the user to run the function below after its hidden by clicking the area it was.
                            if (id != "0"){ // <--- keep this.
                                navController.popBackStack()
                            }

                        }
                        ) {
                            Icon( //Er ikke Material Design 3
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = "Tilbake"
                            )

                        }
                    }
                }
            )
        }
    ){innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            // Should refactor to use "generic" setup screen class as we do alot of copy pasting for now.
            when (id) {

                "0" -> NamesSetupScreen(viewModel,id,navController)
                "1" -> LocationSetupScreen(searchLocationViewModel, id, navController)
                "2" -> AgeSetupScreen(viewModel,id,navController)
                "3" -> NoseSetupScreen(viewModel,id,navController)
                "4" -> BodySetupScreen(viewModel,id,navController)
                "5" -> FurSetupScreen(viewModel,navController)

            }
        }
    }

}















