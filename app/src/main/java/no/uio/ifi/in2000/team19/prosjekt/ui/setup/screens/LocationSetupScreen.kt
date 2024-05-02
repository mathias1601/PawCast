package no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationTextField
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationViewModel


@Composable
fun LocationSetupScreen(
    searchLocationViewModel: SearchLocationViewModel,
    id: String,
    navController: NavHostController
) {


    Column (
        modifier = Modifier
            .fillMaxSize(),
    ) {




        Column (
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Lokasjon")


            Text(
                text = "Hvor ønsker du værmeldinger for?",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            

            
            Spacer(modifier = Modifier.padding(20.dp))
            
            SearchLocationTextField(viewModel = searchLocationViewModel)
        }

        Spacer(modifier = Modifier.padding(10.dp))


        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {

            Text(
                text="Lokasjon blir lagret lokalt på enheten.",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),

                onClick = {
                    navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen.
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )) {
                Text(text = "Neste")
                Icon(Icons.Filled.ChevronRight, contentDescription = "Next")
            }
        }
    }



}


