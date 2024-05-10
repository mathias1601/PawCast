package no.uio.ifi.in2000.team19.prosjekt.ui.home


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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import dev.jeziellago.compose.markdowntext.MarkdownText
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Cords
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.ui.LoadingScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.error.ErrorScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.theme.Measurements
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenManager(
    viewModel: HomeScreenViewModel,
    navController: NavController
) {

    val adviceUiState = viewModel.adviceUiState.collectAsState().value
    val graphUiState = viewModel.graphUiState.collectAsState().value
    val bestTimeUiState = viewModel.bestTimeUiState.collectAsState().value
    val firstYValueUiState = viewModel.firstYValueUiState.collectAsState().value
    val userInfoUiState = viewModel.userInfoUiState.collectAsState().value
    val locationUiState = viewModel.locationUiState.collectAsState().value
    val temperatureUiState = viewModel.temperatureUiState.collectAsState().value
    val dogImage = viewModel.dogImage.collectAsState().value

    val isRefreshing by remember {
        mutableStateOf(false)
    }
    val state = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = { viewModel.loadWeatherForecast()})

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            Box(
                Modifier
                    .fillMaxSize()
                    .pullRefresh(state),
            ) {
                when (adviceUiState) {
                    is AdviceUiState.Success -> {
                        HomeScreen(userInfoUiState, locationUiState, adviceUiState, graphUiState, bestTimeUiState, temperatureUiState, firstYValueUiState , navController, innerPadding, dogImage)
                    }

                    is AdviceUiState.Loading -> {
                        LoadingScreen()
                    }

                    is AdviceUiState.Error -> {
                        ErrorScreen (
                            reason = adviceUiState.errorReason,
                            onReload = { viewModel.loadWeatherForecast() }
                        )
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userInfo: UserInfo,
    location: Cords,
    advice: AdviceUiState.Success,
    graphUiState: CartesianChartModelProducer,
    bestTime: BestTimesForWalk,
    weather: GeneralForecast,
    firstYValueUiState: Int,
    navController: NavController,
    innerPadding: PaddingValues,
    dogImage: String,
) {


    // ============================ TOP BLUE WEATHER SECTION =================================


    // Graident colors from 0% to 50% of height
    val colorStops = arrayOf(
        0.0f to Color(0xFF0080FF),
        0.5f to Color(0xFFFFB1C1),
        0.6f to MaterialTheme.colorScheme.surface // blend at bottom into same color as surface
    )


    // Column containing ALL content of rest of screen.
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colorStops = colorStops,
                )
            )
    ) {
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {


            // TOP CONTENT
            val context = LocalContext.current

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 20.dp, // more top padding to avoid camera on new phones
                        start = 10.dp,
                        end = 10.dp
                    ),

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                val welcomeMsg =
                    if (userInfo.userName == "" || userInfo.dogName == "") stringResource(R.string.welcome_message_unnamed)
                    else stringResource(
                        R.string.welcome_message_named,
                        userInfo.userName,
                        userInfo.dogName
                    )

                Text(
                    text = welcomeMsg,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )

                Spacer(modifier = Modifier.padding(Measurements.BetweenSectionVerticalGap.measurement))


                // Weather Items
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    val drawableName = weather.symbol
                    val drawableId = context.resources.getIdentifier(
                        drawableName,
                        "drawable",
                        context.packageName
                    ) // need to use getIdentifier instead of R.drawable.. because of  the variable name.




                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = "Værsymbol"
                    )
                    Text(
                        text = weather.temperature.toString() + stringResource(R.string.celciues),
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White, // Always white since background is always blue

                    )

                    Text(
                        text = stringResource(R.string.right_now), // Always white since background is always blue
                    )
                }

                // Location Button / Text and Dog avatar
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    ElevatedButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = stringResource(R.string.location)
                        )
                        Text(
                            text = location.shortName,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                    val dogId = context.resources.getIdentifier(
                        dogImage,
                        "drawable",
                        context.packageName
                    ) // need to use getIdentifier instead of R.drawable.. because of  the variable name.


                    Image(
                        painter = painterResource(id = dogId),
                        contentDescription = dogImage, // is formatted like dog_normal, dog_normal_white_sticker, dog_rain_white_sticker ... etc.
                        modifier = Modifier
                            .height(175.dp)
                            .offset(
                                x = (0).dp,
                                y = (50).dp
                            )
                    )
                }

                Spacer(modifier = Modifier.padding(Measurements.WithinSectionVerticalGap.measurement))
            }


            // ================================ MAIN CONTENT =====================.


            // ======INFO OPEN / CLOSE BOXES =============
            var showGraphInfoSheet by remember { mutableStateOf(false) }
            var showAdviceInfoSheet by remember { mutableStateOf(false) }

            // Advice Info Sheet.
            if (showAdviceInfoSheet) {
                ModalBottomSheet(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 200.dp),
                    onDismissRequest = { showAdviceInfoSheet = false }
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = Measurements.HorizontalPadding.measurement)
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

            // Graph Info Sheet
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


            // This is the Surface containing Advice + Graph cards.
            Surface(
                shape = MaterialTheme.shapes.extraLarge

            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = Measurements.HorizontalPadding.measurement,
                            vertical = Measurements.BetweenSectionVerticalGap.measurement
                        ),
                ) {


                    /*
                    ============= ADVICE CARDS =====================================
                    Wrapped in column so advice content is grouped together
                    */

                    Column(
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            Text(
                                text = stringResource(R.string.recomendations_title),
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
                                pageSpacing = Measurements.HorizontalPadding.measurement
                            ) { id ->
                                AdviceCard(
                                    advice = advice.allAdvice[id],
                                    id = id,
                                    navController = navController,
                                    pagerState = pagerState
                                )
                            }
                            // Active card thing. Gray Circles indicating which card is shown.
                            Spacer(modifier = Modifier.padding(2.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // used to have gray circle showing current card, but this lagged quite alot even though it taken from documentation, so we landed on numbers which lags alot less.
                                Text(
                                    text = "${pagerState.currentPage + 1}/${advice.allAdvice.size}",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(Measurements.BetweenSectionVerticalGap.measurement))


                    // =============== GRAPH ==========================
                    Column(
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.graph_title),
                                style = MaterialTheme.typography.titleLarge,
                            )
                            TextButton(onClick = { showGraphInfoSheet = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = stringResource(R.string.info_about_graph)
                                )
                            }
                        }
                        
                        RecomendedTimesForWalk(bestTimesForWalk = bestTime)

                        Spacer(modifier = Modifier.padding(10.dp))
                        
                        ForecastGraph(graphUiState, firstYValueUiState)
                        
                        Spacer(modifier = Modifier.padding(Measurements.BetweenSectionVerticalGap.measurement))

                        BottomInfo(lastUpdated = weather.date)
                        
                        Spacer(modifier = Modifier.padding(Measurements.WithinSectionVerticalGap.measurement)) // spacing to not lock items to top of app bar
                    }
                }
            }
        }
    }
}


@Composable
fun BottomInfo(lastUpdated: LocalDateTime){

    Text(
        text = stringResource(R.string.made_with_data_from_met),
        style = MaterialTheme.typography.bodySmall
    )

    val format = DateTimeFormatter.ofPattern("HH:mm")
    Text(text = stringResource(R.string.last_updated, lastUpdated.format(format)),
        style = MaterialTheme.typography.bodySmall
    )

}


@Composable
fun RecomendedTimesForWalk(bestTimesForWalk: BestTimesForWalk) {


    Card(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = Measurements.HorizontalPadding.measurement,
                    vertical = Measurements.WithinSectionVerticalGap.measurement
                )
                .fillMaxSize()
        ) {



            // When there is no recomened times for a walk
            if (bestTimesForWalk.morning.isBlank() && bestTimesForWalk.midday.isBlank() && bestTimesForWalk.evening.isBlank()){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Measurements.HorizontalPadding.measurement, vertical = Measurements.WithinSectionVerticalGap.measurement),
                ){
                    Icon(imageVector = Icons.Filled.Warning, contentDescription = stringResource(R.string.warning_icon_description))
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(text = stringResource(R.string.bad_weather_alert), modifier = Modifier.fillMaxWidth())
                }

                // Show recomened times
            } else {

                val morningText =
                    if (bestTimesForWalk.morning.isNotBlank()) stringResource( R.string.morning_walk_time, bestTimesForWalk.morning)
                    else stringResource(R.string.morning_walk_is_not_recommended)
                val middayText =
                    if (bestTimesForWalk.midday.isNotBlank()) stringResource(R.string.midday_walk_time, bestTimesForWalk.midday)
                    else stringResource(R.string.midday_walk_not_recommended)
                val eveningText =
                    if (bestTimesForWalk.evening.isNotBlank()) stringResource(R.string.evening_walk_time, bestTimesForWalk.evening)
                    else stringResource(R.string.evening_walk_not_recommened)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = Measurements.HorizontalPadding.measurement,
                            vertical = Measurements.WithinSectionVerticalGap.measurement
                        ),
                ) {
                    Icon(imageVector = Icons.Filled.AccessTime, contentDescription = stringResource(
                        R.string.klokke_ikon_description
                        )
                    )
                    Spacer(modifier = Modifier.padding(5.dp))

                    Column {
                        Text(text = morningText)
                        Text(text = middayText)
                        Text(text = eveningText)
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
            .defaultMinSize(minHeight = Measurements.AdviceCardHeight.measurement)


            // Scroll "animation" changing the cards opacity while scrolling.
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Measurements.HorizontalPadding.measurement)
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

                            MarkdownText(
                                markdown = advice.shortAdvice,
                                style = MaterialTheme.typography.bodyMedium,
                                linkColor = MaterialTheme.colorScheme.onPrimaryContainer)
                        }

                        Button(
                            onClick = {
                                navigateToMoreInfoScreen()
                            },
                            modifier = Modifier.align(Alignment.End),

                        ) {
                            Text(
                                text = stringResource(R.string.read_more)
                            )
                        }
                    }
    }
}

@Composable
fun ForecastGraph(graphUiState: CartesianChartModelProducer, firstYValueUiState: Int) {

    val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) // get hour
    val bottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _->
            var label = time  + x.toInt() // Label = time now + x index... x = 0 = now, x = 1 = in hour hour... formatted as Int 0 <= 36
            if (label > 23){ // Loop around to 00:00
                label -= 24
            }

            if (label < 10){
                "0$label"
            } else  {
                "$label"
            }

        }
    //TODO: flytte til VM
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
            .fillMaxWidth()
            .height(Measurements.GraphHeight.measurement),
    ){
        Column (
            modifier = Modifier.padding(
                horizontal = Measurements.WithinSectionHorizontalGap.measurement,
                vertical = Measurements.WithinSectionVerticalGap.measurement
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            CartesianChartHost(
                modifier = Modifier        // Workaround to enable alpha compositing
                    .fillMaxSize(),
                chart =
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            listOf(
                                rememberLineSpec(
                                    shader = DynamicShaders.color(scoreColor!!
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
                                textAlignment = Layout.Alignment.ALIGN_CENTER,
                                textSize = MaterialTheme.typography.bodyMedium.fontSize
                                ),

                            title = stringResource(R.string.y_axis_title)
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
                                textAlignment = Layout.Alignment.ALIGN_CENTER,
                                textSize = MaterialTheme.typography.bodyMedium.fontSize
                            ),
                            title = stringResource(R.string.x_axis_title),
                            guideline = null
                        ),
                ),
                modelProducer = graphUiState,
                zoomState = rememberVicoZoomState(zoomEnabled = false),
                marker = rememberMarker(),
                horizontalLayout = HorizontalLayout.fullWidth(),
            )
        }
    }
}






