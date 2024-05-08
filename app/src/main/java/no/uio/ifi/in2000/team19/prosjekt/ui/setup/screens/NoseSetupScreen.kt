package no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupScreenViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoseSetupScreen(viewModel: SetupScreenViewModel, id: String, navController: NavHostController) {

    val noseIndex = viewModel.selectedNoseIndex.collectAsState().value

    Column (
        modifier = Modifier
            .fillMaxSize(),
        ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Image(
                painter = painterResource(id = R.drawable.dog_normal),
                contentDescription = "Avatar",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(125.dp)
            )
        }


        Column(
            modifier = Modifier.weight(2f),

        ) {
            Text(
                text="Hva slags snute har hunden din?",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(10.dp))


            FlowRow (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ){


                Card(
                    modifier = Modifier
                        .height(125.dp)
                        .weight(1f)
                        .padding(4.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = if (noseIndex == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    ),

                    onClick = {
                        viewModel.updateNoseIndex(0)
                        viewModel.updateIsFlatNosed(false) // Doesnt need to update puppy in database
                        navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
                    },

                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ){
                        Text(text = "Vanlig snute")
                    }

                }


                Card(
                    modifier = Modifier
                        .height(125.dp)
                        .weight(1f)
                        .padding(4.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = if (noseIndex == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    ),

                    onClick = {
                        viewModel.updateNoseIndex(1)
                        viewModel.updateIsFlatNosed(true) // Doesnt need to update adult in database
                        navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
                    }) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ){
                        Text(text = "Flat snute")
                    }
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Text(
                text= stringResource(R.string.chooseDogCategoryBottomScreenTip),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )


        }
    }
}
