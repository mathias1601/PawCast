package no.uio.ifi.in2000.team19.prosjekt.ui.home


import android.annotation.SuppressLint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.os.Build
import android.text.Layout
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.android.material.shape.RoundedCornerTreatment
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenManager(
    viewModel: HomeScreenViewModel,
    navController: NavController
) {

    val adviceUiState = viewModel.adviceUiState.collectAsState().value
    val cordsUiState = viewModel.cordsUiState.collectAsState().value
    val graphUiState = viewModel.graphUiState.collectAsState().value

    val isRefreshing by remember {
        mutableStateOf(false)
    }
    val state = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = { viewModel.loadWeatherForecast()})

    Scaffold { innerPadding ->

            Box(
                Modifier
                    .padding(innerPadding)
                    .pullRefresh(state),
            ) {
                when (adviceUiState) {
                    is AdviceUiState.Success -> {
                        HomeScreen(adviceUiState, cordsUiState, graphUiState, navController)
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
    graphUiState: CartesianChartModelProducer,
    navController: NavController,
) {

    val newColor = Color(0xffece9e4)

    val context = LocalContext.current
    //val drawableName = advice.
    val drawableId =
        context.resources.getIdentifier("clearsky_day", "drawable", context.packageName)
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {


        Card(
            colors = CardDefaults.cardColors(
                //containerColor = newColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .padding(9.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                //horizontalArrangement = Arrangement.Center, // Horisontalt midtstille alle elementer i raden
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text("18C")

                Spacer(modifier = Modifier.size(15.dp))

                Image(
                    painter = painterResource(id = drawableId),
                    contentDescription = "Værsymbol"
                )
            }
        }
        Text(
            text = "Anbefalinger",
            fontWeight = FontWeight.Bold
        )
        //Text(text = "Valgt lokasjon: ${cords.shortName}")
        //Text("Longitude: ${cords.longitude}, Latitude: ${cords.latitude}")

        LazyColumn(
            Modifier.fillMaxHeight(0.4f)
        ) {
            var id = 0
            items(advice.allAdvice) { item ->
                AdviceCard(item, id, navController)
                id++
            }
        }

        //Spacer(modifier = Modifier.size(50.dp))

        ForecastGraph(graphUiState)
    }
}

@Composable
fun AdviceCard(advice: Advice, id: Int, navController: NavController) {

    Card(
        //colors = CardDefaults.cardColors(
        //containerColor = Color(android.graphics.Color.parseColor(advice.color))
        modifier = Modifier
            .padding(2.dp)
            //.fillMaxHeight(0.3f)
            .clickable {
                navController.navigate("advice/${id.toString()}")
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(text = advice.title)
            Text(text = advice.shortAdvice)

        }

    }

}





@SuppressLint("RestrictedApi")
@Composable
fun ForecastGraph(graphUiState: CartesianChartModelProducer) {

    val colors = arrayOf(Color.Green, Color.Red, Color(174, 198, 207))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .shadow(elevation = 40.dp, shape = RoundedCornerShape(23.dp))
    ){
        Column (
            modifier = Modifier
                .padding(15.dp)
                .zIndex(0f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text("Værvurdering for tur")

            // info sirkel

            CartesianChartHost(
                chart =
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        listOf(
                            rememberLineSpec(
                                shader = DynamicShaders.color(colors[2])
                            )
                        ),
                        axisValueOverrider = AxisValueOverrider.fixed(minY = 0f, maxY = 10f)
                    ),
                    startAxis = rememberStartAxis(
                        titleComponent =
                        rememberTextComponent(
                            background = ShapeComponent(
                                shape = Shapes.pillShape,
                                color = Color(174, 198, 207).hashCode()),
                                padding = MutableDimensions(8f, 1f),
                                textAlignment = Layout.Alignment.ALIGN_CENTER
                        ),
                        title = "Vurdering",
                    ),
                    bottomAxis = rememberBottomAxis(
                        guideline = null,
                        titleComponent =
                        rememberTextComponent(
                            background = ShapeComponent(
                                shape = Shapes.pillShape,
                                color = Color(174, 198, 207).hashCode()),
                                padding = MutableDimensions(8f, 2f)
                        ),
                        title = "Klokkkeslett."
                    ),
                ),
                modelProducer = graphUiState,
                zoomState = rememberVicoZoomState(zoomEnabled = false),
                modifier = Modifier.fillMaxSize(),
                marker = rememberMarker()

            )

        }

    }
}


/*            Image(
                painter = painter,
                contentDescription = "smileys",
                modifier = Modifier.zIndex(2f)


 Image(
        bitmap = imageBitmap,
        contentDescription = null,
        modifier = Modifier
            .size(100.dp, 100.dp)
            .clip(customShape) // Clip to the custom-defined shape




    )




                graphuistate er en lineseries
            )*/



