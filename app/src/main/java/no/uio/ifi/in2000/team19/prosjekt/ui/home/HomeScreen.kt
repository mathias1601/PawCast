package no.uio.ifi.in2000.team19.prosjekt.ui.home


import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.text.Layout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.shape.shader.TopBottomShader
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.ui.LoadingScreen
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenManager(
    viewModel: HomeScreenViewModel,
    navController: NavController
) {

    val adviceUiState = viewModel.adviceUiState.collectAsState().value
    val graphUiState = viewModel.graphUiState.collectAsState().value
    val firstYValueUiState = viewModel.firstYValueUiState.collectAsState().value
    val userInfoUiState = viewModel.userInfoUiState.collectAsState().value
    val locationUiState = viewModel.locationUiState.collectAsState().value
    val temperatureUiState = viewModel.temperatureUiState.collectAsState().value

    val isRefreshing by remember {
        mutableStateOf(false)
    }
    val state = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = { viewModel.loadWeatherForecast(locationUiState)})

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            Box(
                Modifier
                    .fillMaxSize()
                    .pullRefresh(state),
            ) {
                when (adviceUiState) {
                    is AdviceUiState.Success -> {
                        HomeScreen(userInfoUiState, locationUiState, adviceUiState, graphUiState, temperatureUiState, firstYValueUiState , navController, innerPadding)
                    }

                    is AdviceUiState.Loading -> {
                        LoadingScreen()
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userInfo: UserInfo,
    location: Cords,
    advice: AdviceUiState.Success,
    graphUiState: CartesianChartModelProducer,
    weather: GeneralForecast,
    firstYValueUiState: Int,
    navController: NavController,
    innerPadding: PaddingValues,
) {

    // ======INFO OPEN / CLOSE BOXES
    var showGraphInfoSheet by remember { mutableStateOf(false) }
    var showAdviceInfoSheet by remember { mutableStateOf(false)}

    if (showAdviceInfoSheet) {
        ModalBottomSheet(
            modifier = Modifier
            .defaultMinSize(minHeight = 200.dp),
            onDismissRequest = { showAdviceInfoSheet = false }
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Anbefalinger",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.padding(10.dp))
                
                Text(
                    text = stringResource(id = (R.string.adviceinfo)),
                    style = MaterialTheme.typography.bodyLarge
                    )
            }
        }
    }
    if (showGraphInfoSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .defaultMinSize(minHeight = 200.dp),
            onDismissRequest = { showGraphInfoSheet = false }
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Graf-forklaring",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.graphinfo),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }







    // ============================ TOP BLUE WEATHER SECTION =================================
    // Box is outside of Column hierarchy and therefor stretches for the entire
    // size of screen without interfering with content.


    val colorStops = arrayOf(
        0.0f to Color(0xFFF0080FF),
        0.5f to Color(0xFFFFB1C1)
    )


    // MAIN column containing ALL content of rest of screen.

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colorStops = colorStops,
                ),
            )
            ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
        ) {

            Spacer(modifier = Modifier.padding(10.dp)) // Spacer to avoid top app bar.

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                /*
                val welcomeMsg = if (userInfo.userName == "" && userInfo.dogName == "") "Heisann!" else "Heisann ${userInfo.userName} og ${userInfo.dogName}!"
                Text(
                    text = welcomeMsg,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                */

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val context = LocalContext.current
                    val drawableName = weather.symbol
                    val drawableId = context.resources.getIdentifier(drawableName, "drawable", context.packageName) // need to use getIdentifier instead of R.drawable.. because of  the variable name.

                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = "Værsymbol"
                    )

                    Text(
                        text = weather.temperature.toString() + "°C",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,

                        )
                    Text(text = "Akkurat nå")

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
                        painter = painterResource(id = R.drawable.dog_normal),
                        contentDescription = "dog avatar",
                        modifier = Modifier
                            .scale(1f)
                            .offset(
                                x = (0).dp,
                                y = (60).dp
                            )
                    )

                }
            }



            // ================================ SURFACE MAIN CONTENT =====================.
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                ,

                shape = MaterialTheme.shapes.extraLarge

            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {


                /*
                ============= ADVICE CARDS =====================================
                Wrapped in column so advice content is grouped together
                */
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

                            TextButton(onClick = { showAdviceInfoSheet = true }) {
                                Icon(imageVector = Icons.Filled.Info, contentDescription = "")
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
                                    navController = navController,
                                    pagerState = pagerState
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


                    // =============== GRAPH ==========================
                    Column(
                        modifier = Modifier.padding(bottom =  100.dp)
                    ){
                        Spacer(modifier = Modifier.padding(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Beste tidspunkter for tur",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            TextButton(onClick = { showGraphInfoSheet = true }) {
                                Icon(imageVector = Icons.Filled.Info, contentDescription = "Info about graph")
                            }
                        }
                        ForecastGraph(graphUiState, firstYValueUiState)
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdviceCard(advice: Advice, id: Int, navController: NavController, pagerState:PagerState) {

    val navigateToMoreInfoScreen = { navController.navigate("advice/${id.toString()}") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)


            // Gotten from android offical documentation: https://developer.android.com/develop/ui/compose/layouts/pager
            .graphicsLayer {
                val pageOffset = (
                        (pagerState.currentPage - id) + pagerState
                            .currentPageOffsetFraction
                        ).absoluteValue

                // We animate the alpha, between 50% and 100%
                alpha = lerp(
                    start = 0.5f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
            }

    ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                        ,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            Text(
                                text = advice.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            Text(
                                text = advice.shortAdvice,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Button(
                            onClick = {
                                navigateToMoreInfoScreen()
                            },
                            modifier = Modifier.align(Alignment.End),
                            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 8.dp),

                        ) {
                            Text(
                                text = "Les mer"
                            )
                        }
                    }

                }

    }
}

@SuppressLint("RestrictedApi")
@Composable
fun ForecastGraph(graphUiState: CartesianChartModelProducer, firstYValueUiState: Int) {

    // Setting start state to current time
    // val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) // get hour
    //val scrollState = rememberVicoScrollState(
    //    initialScroll = Scroll.Absolute.Companion.x(x = time.toFloat(), bias = 0f)
    // )

    //val hoursOfDay = listOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13","14", "15", "16", "17", "18", "19", "20", "21", "22", "23")
    val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) // get hour

    val bottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _->

            var label = time  + x.toInt() // Label = tid nå + x indeks... x = 0 = tiden nå, x = 1 = om en time... formatert som Int 0 <= 36

            if (label > 23){ // Trekk fra 24 timer dersom
                label -= 24
            }

            if (label < 10){
                "0$label"
            } else  {
                "$label"
            }

        }
    //flytte til vm
    val colorMap: Map<Int, Color> = mapOf(
        1 to Color.Red,
        2 to Color.Red,
        3 to Color(242, 140, 40),
        4 to Color(242, 140, 40),
        5 to Color(242, 140, 40),
        6 to Color.Yellow,
        7 to Color.Yellow,
        8 to Color(76, 187, 23),
        9 to Color(76, 187, 23),
        10 to Color(76, 187, 23)
    )

    val scoreColor: Color? = colorMap[firstYValueUiState]

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ){
        Column (
            modifier = Modifier
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text("Høyere er bedre")
            CartesianChartHost(
                // getXStep = { 1f }, // Show every X step on X axis.
                chart =
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            listOf(
                                rememberLineSpec(
                                    shader = TopBottomShader(
                                        DynamicShaders.color(scoreColor!!),
                                        DynamicShaders.color(scoreColor),
                                    ),
                                )
                            ),
                            axisValueOverrider = AxisValueOverrider.fixed(minY = 0f, maxY = 10f)
                        ),
                        startAxis = rememberStartAxis(
                            titleComponent = rememberTextComponent(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,

                                    background = ShapeComponent(
                                        shape = Shapes.pillShape,
                                        color = MaterialTheme.colorScheme.secondaryContainer.hashCode()
                                    ),

                                    padding = MutableDimensions(8f, 1f),
                                    textAlignment = Layout.Alignment.ALIGN_CENTER
                                ),

                            title = "Score"
                        ),
                        bottomAxis = rememberBottomAxis(
                            itemPlacer = AxisItemPlacer.Horizontal.default(
                                spacing = 1
                            ),
                            labelRotationDegrees = -30f,
                            valueFormatter = bottomAxisValueFormatter,
                            titleComponent = rememberTextComponent(

                                color = MaterialTheme.colorScheme.onSecondaryContainer,

                                background = ShapeComponent(
                                    shape = Shapes.pillShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer.hashCode()
                                ),

                                padding = MutableDimensions(8f, 1f),
                                textAlignment = Layout.Alignment.ALIGN_CENTER
                            ),
                            title = "Klokkkeslett",
                            guideline = null
                        ),
                ),
                modelProducer = graphUiState,
                zoomState = rememberVicoZoomState(zoomEnabled = false),
                modifier = Modifier.fillMaxSize(),
                marker = rememberMarker(),
                horizontalLayout = HorizontalLayout.fullWidth(),
            )
        }
    }
}






