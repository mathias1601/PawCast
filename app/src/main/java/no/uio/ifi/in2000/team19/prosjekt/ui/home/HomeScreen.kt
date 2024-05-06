package no.uio.ifi.in2000.team19.prosjekt.ui.home


import android.annotation.SuppressLint
import android.icu.util.Calendar
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenManager(
    viewModel: HomeScreenViewModel,
    navController: NavController
) {

    viewModel.initialize()

    val adviceUiState = viewModel.adviceUiState.collectAsState().value
    val graphUiState = viewModel.graphUiState.collectAsState().value
    val userInfoUiState = viewModel.userInfoUiState.collectAsState().value
    val locationUiState = viewModel.locationUiState.collectAsState().value
    val temperatureUiState = viewModel.temperatureUiState.collectAsState().value

    val isRefreshing by remember {
        mutableStateOf(false)
    }
    val state = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = { viewModel.initialize()})

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            Box(
                Modifier
                    .fillMaxSize()
                    .pullRefresh(state),
            ) {
                when (adviceUiState) {
                    is AdviceUiState.Success -> {
                        HomeScreen(userInfoUiState, locationUiState, adviceUiState, graphUiState, temperatureUiState, navController, innerPadding)
                    }

                    is AdviceUiState.Loading -> {
                        Column(
                            Modifier.padding(innerPadding),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }

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
    temperature: GeneralForecast,
    navController: NavController,
    innerPadding: PaddingValues,
) {

    // ======INFO OPEN / CLOSE BOXES
    var showGraphInfoSheet by remember { mutableStateOf(false) }
    var showAdviceInfoSheet by remember { mutableStateOf(false)}

    if (showAdviceInfoSheet) {
        ModalBottomSheet(
            modifier = Modifier
            .defaultMinSize(minHeight = 200.dp)
            ,
            onDismissRequest = { showAdviceInfoSheet = false }
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Anbefalinger",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Lorem ipsium er en ......",
                    style = MaterialTheme.typography.bodyMedium
                    )
            }
        }
    }
    if (showGraphInfoSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .defaultMinSize(minHeight = 200.dp)
            ,
            onDismissRequest = { showGraphInfoSheet = false }
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Graf!!",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "grafer er kult :D",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }







    // ============================ TOP BLUE WEATHER SECTION =================================
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
            .fillMaxSize()
            .padding(innerPadding),
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
                text = if (userInfo.userName != "" && userInfo.dogName != ""){
                    "Heisann ${userInfo.userName} og ${userInfo.dogName}!"
                    } else {
                       "Heisann!"
                    }
                ,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )


            // WEATHER ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = temperature.temperature.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,

                    )

                val context = LocalContext.current
                val drawableId = context.resources.getIdentifier("clearsky_day", "drawable", context.packageName)

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
                            x = (-10).dp,
                            y = (-5).dp
                        )
                )

            }
        }






        // ================================ SURFACE MAIN CONTENT =====================.
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .weight(3f)
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
                    .verticalScroll(rememberScrollState()) // <-- Makes
                    .padding(20.dp)
                    .fillMaxSize(),
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            ) {
                            Text(
                                text = "Anbefalinger",
                                style = MaterialTheme.typography.titleLarge,
                            )

                            TextButton(onClick = { showAdviceInfoSheet = true }) {
                                Icon(imageVector = Icons.Filled.Info, contentDescription = "")
                            }
                        }

                        
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
                    ForecastGraph(graphUiState)
                }
            }
        }
    }
}



@Composable
fun AdviceCard(advice: Advice, id: Int, navController: NavController) {

    val navigateToMoreInfoScreen = { navController.navigate("advice/${id.toString()}") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable {
                navigateToMoreInfoScreen()
            }
    ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer
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
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            Text(
                                text = advice.shortAdvice,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Button(
                            onClick = {
                                navigateToMoreInfoScreen()
                            },
                            modifier = Modifier.align(Alignment.End),
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
               label = label - 24
            }

            if (label < 10){
                "0$label:00"
            } else  {
                "$label:00"
            }

        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ){
        Column (
            modifier = Modifier
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text("Værvurdering for tur")

            CartesianChartHost(
                // getXStep = { 1f }, // Show every X step on X axis.
                chart =
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            listOf(
                                rememberLineSpec(
                                    shader = TopBottomShader(
                                        DynamicShaders.color(Color.Green),
                                        DynamicShaders.color(Color.Blue),
                                    ),
                                )
                            ),
                            axisValueOverrider = AxisValueOverrider.fixed(minY = 0f, maxY = 10f)
                        ),
                        startAxis = rememberStartAxis(
                            titleComponent =
                                rememberTextComponent(
                                    background = ShapeComponent(
                                        shape = Shapes.pillShape,
                                        color = MaterialTheme.colorScheme.tertiary
                                        .hashCode()),
                                        padding = MutableDimensions(8f, 1f),
                                        textAlignment = Layout.Alignment.ALIGN_CENTER
                                ),

                            title = "Vurdering",
                        ),
                        bottomAxis = rememberBottomAxis(
                            itemPlacer = AxisItemPlacer.Horizontal.default(
                                spacing = 2
                            ),
                            labelRotationDegrees = -30f,
                            valueFormatter = bottomAxisValueFormatter,
                            titleComponent = rememberTextComponent(
                                    background = ShapeComponent(
                                        shape = Shapes.pillShape,
                                        color =  MaterialTheme.colorScheme.tertiary.hashCode()),
                                        padding = MutableDimensions(8f, 2f)
                            ),
                            title = "Klokkkeslett.",
                            guideline = null
                        ),
                ),
                modelProducer = graphUiState,
                zoomState = rememberVicoZoomState(zoomEnabled = false),
                modifier = Modifier.fillMaxSize(),
                marker = rememberMarker(),
                horizontalLayout = HorizontalLayout.fullWidth(),
                // scrollState = scrollState




            )

        }

    }
}






