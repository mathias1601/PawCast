package no.uio.ifi.in2000.team19.prosjekt.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.ui.home.AdviceUiState
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel


@Composable
fun HomeScreenManager(){
    val viewModel: HomeScreenViewModel = viewModel()
    val adviceUiState = viewModel.adviceUiState.collectAsState().value


    Scaffold(
        bottomBar = {
            BottomAppBar {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = { /* TODO */ }) {
                            Text(text = "Home")
                        }
                        Button(onClick = { /*TODO */ }) {
                            Text(text = "Settings")
                        }
                    }

            }
        }
    ) {innerPadding ->

        Column(
            Modifier.padding(innerPadding)
        ) {
            when(adviceUiState ) {
                is AdviceUiState.Success -> {
                    HomeScreen(adviceUiState)
                }
                is AdviceUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is AdviceUiState.Error -> {
                    //NoConnectionScreen()
                }
            }
        }
    }


}

@Composable
fun NoConnectionScreen(){}

/*
*
* Card composable som viser innhold i Advice objekt.
* Viser Advice fra listen hentet i HomeScreenViewModel
*
*/

@Composable
fun HomeScreen(adviceUiState: AdviceUiState.Success) {

    val advices = adviceUiState.allAdvice

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Anbefalinger"
        )
        LazyColumn(
        ) {
            items(advices) { item ->
                AdviceCard(item)
            }
        }
    }
}

@Composable
fun AdviceCard(advice: Advice){

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(advice.color))
        ),
        modifier = Modifier
            .padding(2.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ){
            Text(text = advice.title)
            Text(text = advice.description)
            Text(text = "${advice.forecast.temperature} grader")
            Text(text = "${advice.forecast.windspeed} m/s")
        }
    }

}

