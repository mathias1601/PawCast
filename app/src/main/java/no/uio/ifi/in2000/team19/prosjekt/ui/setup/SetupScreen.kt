package no.uio.ifi.in2000.team19.prosjekt.ui.setup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.annotation.SuppressLint as SuppressLint1


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
                "2" -> OnboardingScreenThree(viewModel,navController)

            }
        }
    }

}

@Composable
fun OnboardingScreenOne(viewModel: SetupScreenViewModel, id:String, navController: NavHostController) {

    var userName by remember { mutableStateOf("") }
    var dogName by remember { mutableStateOf("") }

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
            Button(onClick = {
                viewModel.updateUserName(userName)
                viewModel.updateDogName(dogName)
                navController.navigate("setup/${id.toInt()+1}")
            }) {
                Text(text = "Neste")
            }
        }
        //Skal også kunne skrive inn adresse en eller annen gang i setup
    }
}

@SuppressLint1("UnrememberedMutableState")
@Composable
fun OnboardingScreenTwo(viewModel: SetupScreenViewModel, id: String, navController: NavHostController) {

    var age by remember { mutableStateOf(false) }
    var nose by remember { mutableStateOf(false) }
    var body by remember { mutableStateOf(false) }

    val ageOptions = listOf("Valp", "Voksen", "Senior")
    val noseOptions = listOf("Flat", "Ikke flat")
    val bodyOptions = listOf("Tynn", "Middels", "Tykk")

    val (selectedAgeOption, onAgeOptionSelected) = remember { mutableStateOf(ageOptions[0] ) }
    val (selectedNoseOption, onNoseOptionSelected) = remember { mutableStateOf(noseOptions[0] ) }
    val (selectedBodyOption, onBodyOptionSelected) = remember { mutableStateOf(bodyOptions[0] ) }

    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center

    ) {
        Text(text="Alder")
        Row (
            modifier = Modifier
                .fillMaxWidth()
        ){
            ageOptions.forEach { text ->

                CheckboxButton(
                    text = text,
                    isChecked = (text == selectedAgeOption),
                    onClick = {
                        onAgeOptionSelected(text)
                    }
                )

            }
        }
        Text(text="Snute")
        Row (
            modifier = Modifier
                .fillMaxWidth()
        ){

            noseOptions.forEach { text ->

                CheckboxButton(
                    text = text,
                    isChecked = (text == selectedNoseOption),
                    onClick = {
                        onNoseOptionSelected(text)
                    }
                )

            }
        }
        Text(text="Kropp")
        Row (
            modifier = Modifier
                .fillMaxWidth()
        ){
            bodyOptions.forEach { text ->

                CheckboxButton(
                    text = text,
                    isChecked = (text == selectedBodyOption),
                    onClick = {
                        onBodyOptionSelected(text)
                    }
                )

            }
        }

        Box() {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    age = selectedAgeOption == "Senior"
                    nose = selectedNoseOption == "Flat"
                    body = selectedBodyOption == "Tynn"

                    viewModel.updateAge(age)
                    viewModel.updateNose(nose)
                    viewModel.updateThin(body)

                    navController.navigate("setup/${id.toInt()+1}")
                }
            ) {
                Text(text = "Neste")
            }
        }
    }

}
@Composable
fun CheckboxButton(
    text: String,
    isChecked: Boolean,
    onClick: (Boolean) -> Unit

) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick(isChecked) }

    ) {
        Box(
            modifier = Modifier

        )
        Checkbox(checked = isChecked, onCheckedChange = onClick)
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.merge()
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreenThree(viewModel: SetupScreenViewModel, navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text="Pels")
        FlowRow() {

            FilterChip(text = "Tynn", categoryName = "tynnPels", viewModel)
            FilterChip(text = "Tykk", categoryName = "tykkPels", viewModel)
            FilterChip(text = "Lang", categoryName = "langPels", viewModel)
            FilterChip(text = "Kort", categoryName = "kortPels", viewModel)
            FilterChip(text = "Lys", categoryName = "lysPels", viewModel)
            FilterChip(text = "Mørk", categoryName = "moerkPels", viewModel)
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
    ) {
        var selected by remember { mutableStateOf(false)}

        FilterChip(
            modifier = Modifier
                .padding(8.dp),
            onClick = {
                selected = !selected
                viewModel.updateFilterCategories(categoryName,selected)
                },
            label = {
                Text(text=text)
            },
        selected = selected,
        leadingIcon = if (selected) {
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


