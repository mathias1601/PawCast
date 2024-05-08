package no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupScreenViewModel

@Composable
fun NamesSetupScreen(viewModel: SetupScreenViewModel, id:String, navController: NavHostController) {


    val userInfo = viewModel.userInfo.collectAsState().value

    var userName by remember {
        mutableStateOf(userInfo.userName)
    }

    var dogName by remember {
        mutableStateOf(userInfo.dogName)
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween

        ) {

        Column (
            verticalArrangement = Arrangement.Center
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally

            ){

                Image(
                    painter = painterResource(id = R.drawable.dog_normal),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(100.dp)
                )

                Text(
                    text = "Heisann!",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier=Modifier.padding(16.dp))


            Spacer(modifier = Modifier.padding(30.dp))

            Text(
                text = "Først, hva er navnet ditt og navnet til hunden din?",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.padding(10.dp))

            Text(text="Navnet ditt", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = userName,
                maxLines = 1,
                leadingIcon = { Icon(imageVector = Icons.Filled.Person, contentDescription = "Icon of person")},
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                onValueChange = {userName = it},
                label = { Text("Ditt navn") }
            )
            Spacer(modifier=Modifier.padding(10.dp))
            Text(text="Hunden din", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dogName,
                maxLines = 1,
                leadingIcon = { Icon(imageVector = Icons.Filled.Pets, contentDescription = "Icon of person")},
                onValueChange = {dogName = it},
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {

                        keyboardController?.hide()

                        if (userName != ""){
                            viewModel.updateUserName(userName)
                            viewModel.updateDogName(dogName)
                            navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
                        }
                    }
                ),

                label = { Text("Hundens navn") }
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text="Navn blir kun brukt til å personalisere appen for deg.",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth())

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = (userName != "" && dogName != ""),

                onClick = {
                    viewModel.updateUserName(userName)
                    viewModel.updateDogName(dogName)
                    navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )) {
                Text(text = "Neste")
                Icon(Icons.Filled.ChevronRight, contentDescription = "Next")
            }

            TextButton(
                onClick = {
                viewModel.updateUserName("")
                viewModel.updateDogName("")
                navController.navigate("setup/${id.toInt()+1}") })
                {
                Text(text = "Hopp over dette steget")
            }
        }
    }
}