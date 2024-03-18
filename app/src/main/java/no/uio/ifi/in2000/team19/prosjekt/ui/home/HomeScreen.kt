package no.uio.ifi.in2000.team19.prosjekt.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice


@Composable
fun HomeScreenManager(viewModel: HomeScreenViewModel) {


    val adviceUiState = viewModel.adviceUiState.collectAsState().value


    Scaffold(
        bottomBar = {

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
                    NoConnectionScreen()
                }
            }
        }
    }
}

@Composable
fun NoConnectionScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ExtendedFloatingActionButton(
            text = { Text("Ingen internett-tilgang") },
            icon = { Icon(Icons.Filled.Warning, contentDescription = "Advarsel") },
            onClick = { /* TODO change later if we want to update */ }
        )
    }
}

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

