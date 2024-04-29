package no.uio.ifi.in2000.team19.prosjekt.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


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
                "0" -> OnboardingScreenOne(viewModel,id,navController)
                "1" -> OnboardingScreenTwo(viewModel,id,navController)
                "2" -> OnboardingScreenThree(viewModel,id,navController)
                "3" -> OnboardingScreenFour(viewModel,id,navController)
                "4" -> OnboardingScreenFive(viewModel,navController)

            }
        }
    }

}


// DONE.
@Composable
fun OnboardingScreenOne(viewModel: SetupScreenViewModel, id:String, navController: NavHostController) {


    val userInfo = viewModel.userInfo.collectAsState().value

    var userName by remember {
        mutableStateOf(userInfo.userName)
    }

    var dogName by remember {
        mutableStateOf(userInfo.dogName)
    }

    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center


    ) {
        Text(text="Navnet ditt")
        OutlinedTextField(
            value = userName,
            onValueChange = {userName = it},
            label = { Text("Skriv inn") }
        )
        Text(text="Hunden din")
        OutlinedTextField(
            value = dogName,
            onValueChange = {dogName = it},
            label = { Text("Skriv inn") }
        )

        Row (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
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
            }
        }
        //Skal også kunne skrive inn adresse en eller annen gang i setup
    }
}

@Composable
fun OnboardingScreenTwo(viewModel: SetupScreenViewModel, id: String, navController: NavHostController) {

    val ageIndex = viewModel.selectedAgeIndex.collectAsState().value

    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,

    ) {
        Text(text="Alder")
        Row (
            modifier = Modifier
                .fillMaxWidth()
        ){


            Card(
                onClick = {
                viewModel.updateAgeIndex(0)
                viewModel.updateIsSenior(false) // Doesnt need to update puppy in database
                navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
                },

            ) {
                Text(text = "Valp")
            }


            Card(onClick = {
                viewModel.updateAgeIndex(1)
                viewModel.updateIsSenior(false) // Doesnt need to update adult in database
                navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
            }) {
                Text(text = "Voksen")
            }
            
            Card(onClick = {
                viewModel.updateAgeIndex(2)
                viewModel.updateIsSenior(true) // Update senior in database
                navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
            }) {
                Text(text = "Senior")
                
            }
        }
    }
}



@Composable
fun OnboardingScreenThree(viewModel: SetupScreenViewModel, id: String, navController: NavHostController) {

    val noseIndex = viewModel.selectedAgeIndex.collectAsState().value

    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,

        ) {
        Text(text="Nese form")
        Row (
            modifier = Modifier
                .fillMaxWidth()
        ){


            Card(
                onClick = {
                    viewModel.updateAgeIndex(0)
                    viewModel.updateIsFlatNosed(false) // Doesnt need to update puppy in database
                    navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
                },

                ) {
                Text(text = "Vanlig nese")
            }


            Card(onClick = {
                viewModel.updateAgeIndex(1)
                viewModel.updateIsFlatNosed(true) // Doesnt need to update adult in database
                navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
            }) {
                Text(text = "Flat nese")
            }
        }
    }
}

@Composable
fun OnboardingScreenFour(viewModel: SetupScreenViewModel, id: String, navController: NavHostController) {

    val thinIndex = viewModel.selectedThinIndex.collectAsState().value

    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,

        ) {
        Text(text="Nese form")
        Row (
            modifier = Modifier
                .fillMaxWidth()
        ){


            Card(
                onClick = {
                    viewModel.updateThinIndex(0)
                    viewModel.updateIsThin(true) // Doesnt need to update puppy in database
                    navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
                },

                ) {
                Text(text = "Tynn")
            }


            Card(onClick = {
                viewModel.updateThinIndex(1)
                viewModel.updateIsThin(false) // Doesnt need to update adult in database
                navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
            }) {
                Text(text = "Middels")
            }

            Card(onClick = {
                viewModel.updateThinIndex(2)
                viewModel.updateIsThin(false) // Doesnt need to update adult in database
                navController.navigate("setup/${id.toInt()+1}") // Navigate to next screen
            }) {
                Text(text = "Tykk")
            }
        }
    }
}










@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreenFive(viewModel: SetupScreenViewModel, navController: NavHostController) {

    val userInfo = viewModel.userInfo.collectAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text="Pels")
        FlowRow(
        ) {

            FilterChip(text = "Tynn", categoryName = "tynnPels", viewModel, userInfo.isThinHaired)
            /*
            FilterChip(text = "Tykk", categoryName = "tykkPels", viewModel)
            FilterChip(text = "Lang", categoryName = "langPels", viewModel)
            FilterChip(text = "Kort", categoryName = "kortPels", viewModel)
            FilterChip(text = "Lys", categoryName = "lysPels", viewModel)
            FilterChip(text = "Mørk", categoryName = "moerkPels", viewModel)

             */
        }
        Box(){
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    viewModel.saveUserInfo() // save user info to DB
                    viewModel.saveSetupState(isCompleted = true) // store info that setup is completed so next app launch doesnt ask for setup.
                    // navController.popBackStack() // removes history from backstack. Stops user from being able to click back, navigating the user back to setup 👎
                    navController.navigate("home")

                }
            ) {
                Text (text="Fullfør")
            }
        }
    }

}


@Composable
fun FilterChip(
    text: String,
    categoryName: String,
    viewModel: SetupScreenViewModel,
    selected: Boolean
    ) {

        var isSelected by remember {
            mutableStateOf(selected)
        }

        FilterChip(
            modifier = Modifier
                .padding(8.dp),
            onClick = {
                isSelected = !isSelected
                viewModel.updateFilterCategories(categoryName,isSelected)
                },
            label = {
                Text(
                    text=text
                )
            },
        selected = isSelected,
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}


