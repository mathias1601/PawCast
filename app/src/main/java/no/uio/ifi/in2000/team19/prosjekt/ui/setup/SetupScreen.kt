package no.uio.ifi.in2000.team19.prosjekt.ui.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun SetupManager(
    viewModel: SetupScreenViewModel,
    id: String,
    navController: NavHostController
) {
    val userInfo = viewModel.userInfo.collectAsState().value

    Scaffold {innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Text(text = id)
            when (id) {
                "0" -> NameScreen(viewModel,id,navController)
                "1" -> CategoriesScreen(viewModel,id,navController)

            }
        }
    }

}

@Composable
fun NameScreen(viewModel: SetupScreenViewModel, id:String, navController:NavHostController) {

    var userName by remember { mutableStateOf("")}
    var dogName by remember { mutableStateOf("")}

    Column () {
        OutlinedTextField(
            value = userName,
            onValueChange = {userName = it},
            label = { Text("Navnet ditt") }
        )
        OutlinedTextField(
            value = dogName,
            onValueChange = {dogName = it},
            label = { Text("Hunden din") }
        )
    }
    Row () {

        Button(onClick = {
            viewModel.updateUserName(userName)
            viewModel.updateDogName(dogName)
            navController.navigate("setup/${id.toInt()+1}")
        }) {
            Text(text = "Right")
        }
    }
}

@Composable
fun CategoriesScreen(viewModel: SetupScreenViewModel, id: String, navController: NavHostController) {
    Button(onClick = {navController.navigate("setup/${id.toInt()-1}")}) {
        Text(text = "Left")
    }
    Button(onClick = {

        viewModel.saveUserInfo() // save user info to DB
        viewModel.saveSetupState(isCompleted = true) // store info that setup is completed so next app launch doesnt ask for setup.
        // navController.popBackStack() // removes history from backstack. Stops user from being able to click back, navigating the user back to setup 👎
        navController.navigate("home")
    }) {
        Text(text = "Right")
    }
}
