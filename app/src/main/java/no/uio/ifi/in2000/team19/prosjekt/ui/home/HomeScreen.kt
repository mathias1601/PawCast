package no.uio.ifi.in2000.team19.prosjekt.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreenViewModel



@Composable
fun HomeScreenManager(
    viewModel: HomeScreenViewModel,
    settingsViewModel: SettingsScreenViewModel
) {

    val coordsUiState = settingsViewModel.coordinates.collectAsState().value
    val adviceUiState = viewModel.adviceUiState.collectAsState().value


    viewModel.loadWeatherForecast()


    Scaffold(
        bottomBar = {

        }
    ) { innerPadding ->

        Column(
            Modifier.padding(innerPadding)
        ) {
            when (adviceUiState) {
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

            // Unsure of how we want to solve this
            /*
            when (weatherForecastUiState) {
                is WeatherForecastUiState.Success ->
                    WeatherForecast(weatherForecastUiState = weatherForecastUiState)

                is WeatherForecastUiState.Loading ->
                    CircularProgressIndicator()

                is WeatherForecastUiState.Error ->
                    NoConnectionScreen()

            }

             */
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
            items(adviceUiState.allAdvice) { item ->
                AdviceCard(item)
            }
        }

        Spacer(modifier = Modifier.size(50.dp))

        WeatherForecast(adviceUiState.weatherForecast)
    }
}

@Composable
fun AdviceCard(advice: Advice) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(advice.color))
        ),
        modifier = Modifier
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(text = advice.title)
            Text(text = advice.description)
            Text(text = "${advice.forecast.temperature} grader")
            Text(text = "${advice.forecast.windspeed} m/s")
        }
    }

}


@Composable
fun WeatherForecast(weatherForecast: List<GeneralForecast>) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        //columns = GridCells.Adaptive(minSize = 150.dp),
        content = {
            items(weatherForecast) { generalForecast ->
                WeatherForecastCard(generalForecast = generalForecast)
            }
        }
    )
}

@SuppressLint("DiscouragedApi")
@Composable
fun WeatherForecastCard(generalForecast: GeneralForecast) {

    val newColor = Color(android.graphics.Color.parseColor("#ece9e4"))

    val context = LocalContext.current
    val drawableName = generalForecast.symbol
    val drawableId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = newColor
        ),
        modifier = Modifier
            .size(width = 350.dp, height = 75.dp)
            .padding(9.dp)
        //.height(23.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize(),
            //horizontalArrangement = Arrangement.Center, // Horisontalt midtstille alle elementer i raden
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.size(15.dp))

            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "Værsymbol"
            )

            Spacer(modifier = Modifier.size(26.dp))

            Text(
                text = "${generalForecast.temperature}°C",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.size(40.dp))

            Text(
                text = "${generalForecast.wind} m/s",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(35.dp))

            Text(
                text = generalForecast.time,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview
@Composable
fun WeatherForecastPreview() {
    val generalForecast: GeneralForecast = GeneralForecast("22", "10", "clearsky_day", "12:32")
    WeatherForecastCard(generalForecast)

}

