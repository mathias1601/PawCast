package no.uio.ifi.in2000.team19.prosjekt.ui.setup

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.AgeSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.BodySetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.FurSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.NamesSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.NoseSetupScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupManager(
    viewModel: SetupScreenViewModel,
    id: String,
    navController: NavHostController
) {

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = "CocoMilo"
                    )
                },
                navigationIcon = {
                    if (id != "0"){
                        IconButton(onClick = {
                            navController.popBackStack()
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
            when (id) {
                "0" -> NamesSetupScreen(viewModel,id,navController)
                "1" -> AgeSetupScreen(viewModel,id,navController)
                "2" -> NoseSetupScreen(viewModel,id,navController)
                "3" -> BodySetupScreen(viewModel,id,navController)
                "4" -> FurSetupScreen(viewModel,navController)

            }
        }
    }

}















