package no.uio.ifi.in2000.team19.prosjekt.ui.home


import android.annotation.SuppressLint
import android.os.Build
import android.text.Layout
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
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
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenManager(
    viewModel: HomeScreenViewModel,
    navController: NavController
) {

    val adviceUiState = viewModel.adviceUiState.collectAsState().value
    val graphUiState = viewModel.graphUiState.collectAsState().value
    val userInfoUiState = viewModel.userInfoUiState.collectAsState().value
    val locationUiState = viewModel.locationUiState.collectAsState().value

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
                        HomeScreen(userInfoUiState, locationUiState, adviceUiState, graphUiState, navController)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    userInfo: UserInfo,
    location: Cords,
    advice: AdviceUiState.Success,
    graphUiState: CartesianChartModelProducer,
    navController: NavController,
) {


    val context = LocalContext.current
    //val drawableName = advice.
    val drawableId = context.resources.getIdentifier("clearsky_day", "drawable", context.packageName)

    // Box is outside of Column hierarchy and therefor stretches for the entire
    // size of screen without interfering with content.


    val colorStops = arrayOf(
        0.0f to Color.Blue, // Top app bar is specified as this color. To change that color go to Themes.kt and change TOP_APP_BAR_COLOR
        0.5f to Color.Magenta
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = colorStops
                ),
            )
    )



    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.padding(10.dp)) // Spacer to avoid top app bar.

        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Heisann ${userInfo.userName} og ${userInfo.dogName}!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )


            // ACTUAL WEATHER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    ,

                //horizontalArrangement = Arrangement.Center, // Horisontalt midtstille alle elementer i raden
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "18C",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,

                    )


                Image(
                    painter = painterResource(id = drawableId),
                    contentDescription = "Værsymbol"
                )
            }
            
            // Location Button / Text and Dog avatar
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
                ) {

                ElevatedButton(onClick = { navController.navigate("settings") }) {
                    Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Location")
                    Text(
                        text = location.shortName,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.dog),
                    contentDescription = "dog avatar",
                    modifier = Modifier
                        .scale(3f)
                        .offset(
                            x= (-10).dp,
                            y= (-5).dp
                        )
                )

            }
        }


        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            modifier = Modifier
                .weight(3f)
                .shadow(10.dp)

            ,
            shape = RoundedCornerShape(
                topEnd = 23.dp,
                topStart = 23.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            )

        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {


                // ============= ADVICE CARDS =====================================
                Column(

                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {


                        Text(
                            text = "Anbefalinger",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        
                        TextButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.align(Alignment.Bottom)
                        ) {
                            Text(
                                text = "Vis alle",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }



                    // ADVICE CARDS / Horizontal Pager / Carousell + Indicator for card index
                    // Inspired by offical documentaion: https://developer.android.com/develop/ui/compose/layouts/pager
                    Column {
                        val pagerState = rememberPagerState(pageCount = {
                            advice.allAdvice.size
                        })

                        // Advice cards / Horizontal Pager
                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = 20.dp
                        ) { id ->
                            AdviceCard(
                                advice = advice.allAdvice[id],
                                id = id,
                                navController = navController
                            )
                        }
                        // Active card thing. Seems to lag emulator quite alot..
                        Spacer(modifier = Modifier.padding(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(pagerState.pageCount){iteration ->
                                val color = if (pagerState.currentPage == iteration) Color.Gray else Color.LightGray
                                Box (
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(7.dp)
                                )
                            }
                        }


                    }
                }

                Column(
                ) {
                    Text(
                        text = "Beste tidspunkter for tur",
                        style = MaterialTheme.typography.titleMedium,
                        )
                    ForecastGraph(graphUiState)
                }

            }
        }
    }
}



@Composable
fun AdviceCard(advice: Advice, id: Int, navController: NavController) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            // .padding(horizontal = 10.dp)
            .clickable {
                navController.navigate("advice/${id.toString()}")
            }
    ) {
                Box {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = advice.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.size(10.dp))

                        Text(
                            text = advice.shortAdvice,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Button(
                            onClick = { },
                            modifier = Modifier,
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp)
                        ) {
                            Text("Les mer")
                        }

                    }

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
            .fillMaxHeight(1f)
            // .shadow(elevation = 40.dp, shape = RoundedCornerShape(23.dp))
    ){
        Column (
            modifier = Modifier
                .padding(15.dp),
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




