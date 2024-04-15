package no.uio.ifi.in2000.team19.prosjekt.ui.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
                "0" -> StageOne(viewModel,id,navController)
            }

        }
    }

}

@Composable
fun StageOne(setupScreenViewModel: SetupScreenViewModel, id:String, navController:NavHostController) {
    Row () {
        Button(onClick = {navController.navigate("setup/${id.toInt()-1}")}) {
            Text(text = "Left")
        }
        Button(onClick = {navController.navigate("setup/${id.toInt()+1}")}) {
            Text(text = "Right")
        }
    }
}
