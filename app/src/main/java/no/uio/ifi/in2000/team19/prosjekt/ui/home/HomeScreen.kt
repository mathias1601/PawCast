package no.uio.ifi.in2000.team19.prosjekt.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherForecastCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenManager(
    viewModel: HomeScreenViewModel
) {

    val adviceUiState = viewModel.adviceUiState.collectAsState().value
    val cordsUiState = viewModel.cordsUiState.collectAsState().value
    val graphUiState = viewModel.graphUiState.collectAsState().value

    val isRefreshing by remember {
        mutableStateOf(false)
    }
    val state = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = { viewModel.loadWeatherForecast()})

    Scaffold() { innerPadding ->

            Box(
                Modifier.padding(innerPadding)
                        .pullRefresh(state),
            ) {
                when (adviceUiState) {
                    is AdviceUiState.Success -> {
                        HomeScreen(adviceUiState, cordsUiState, graphUiState)
                    }

                    is AdviceUiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is AdviceUiState.Error -> {
                        NoConnectionScreen()
                    }
                }

            PullRefreshIndicator(
                refreshing = isRefreshing, state = state,
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
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
fun HomeScreen(
    advice: AdviceUiState.Success,
    cords: Cords,
    graphUiState: CartesianChartModelProducer
) {


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Anbefalinger"
        )
        Text(text = "Valgt lokasjon: ${cords.shortName}")
        LazyColumn(
        ) {
            items(advice.allAdvice) { item ->
                AdviceCard(item)
            }
        }

        Spacer(modifier = Modifier.size(50.dp))

        ForecastGraph(graphUiState)
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

@Composable
fun ForecastGraph(graphUiState: CartesianChartModelProducer) {


     // burde flyttes til viewmodel https://patrykandpatrick.com/vico/wiki/cartesian-charts/data (se første warning)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(elevation = 40.dp, shape = RoundedCornerShape(23.dp))
    ){
        Column (modifier = Modifier.padding(15.dp)){
            CartesianChartHost(
                chart =
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        listOf(
                            rememberLineSpec(
                                DynamicShaders.color(Color(0xFF128DDF)))
                        )),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(guideline = null),
                ),
                modelProducer = graphUiState,
                zoomState = rememberVicoZoomState(zoomEnabled = false),

                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
