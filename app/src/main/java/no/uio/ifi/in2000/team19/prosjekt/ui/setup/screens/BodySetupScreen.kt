package no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupScreenViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BodySetupScreen(viewModel: SetupScreenViewModel, id: String, navController: NavHostController) {

    val thinIndex = viewModel.selectedThinIndex.collectAsState().value

    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,

        ) {

        Column(
              modifier = Modifier
                  .weight(2f),
            verticalArrangement = Arrangement.Center
                ){

            Text(text="Hvilken beskriver hunden din best?",
                style = MaterialTheme.typography.titleLarge)
            FlowRow (
                modifier = Modifier
                    .fillMaxWidth()
            ) {


                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (thinIndex == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    ),
                    onClick = {
                        viewModel.updateThinIndex(0)
                        viewModel.updateIsThin(true) // Doesnt need to update puppy in database
                        navController.navigate("setup/${id.toInt() + 1}") // Navigate to next screen
                    },

                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = "Tynn")
                    }

                }


                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = if (thinIndex == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    ),

                    onClick = {
                        viewModel.updateThinIndex(1)
                        viewModel.updateIsThin(false) // Doesnt need to update adult in database
                        navController.navigate("setup/${id.toInt() + 1}") // Navigate to next screen
                    }) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = "Middels")
                    }

                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = if (thinIndex == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    ),

                    onClick = {
                        viewModel.updateThinIndex(2)
                        viewModel.updateIsThin(false) // Doesnt need to update adult in database
                        navController.navigate("setup/${id.toInt() + 1}") // Navigate to next screen
                    }) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = "Tykk")
                    }

                }


            }


        }
        Column (
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(text="Vi vil bruke hundekategoriene du har valgt til å gi deg spesifiserte værmeldinger og anbefalinger.")
        }



    }
}