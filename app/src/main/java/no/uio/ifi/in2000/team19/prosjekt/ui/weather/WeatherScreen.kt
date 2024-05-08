package no.uio.ifi.in2000.team19.prosjekt.ui.weather

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay
import no.uio.ifi.in2000.team19.prosjekt.ui.home.NoConnectionScreen

@Composable
fun WeatherScreen(weatherScreenViewModel: WeatherScreenViewModel, innerPadding:PaddingValues, navController: NavController) {


    when (val weatherUiState = weatherScreenViewModel.weatherUiState.collectAsState().value) {
        is WeatherUiState.Loading -> CircularProgressIndicator()
        is WeatherUiState.Error -> NoConnectionScreen()
        is WeatherUiState.Success -> {


            val location = weatherScreenViewModel.locationUiState.collectAsState().value

            val weatherHours = weatherUiState.weather.general
            val weatherDays = weatherUiState.weather.day
            val weatherMean = weatherUiState.weather.hours

            val allHours = weatherHours.drop(1)
            
            val differentDays = weatherDays.map { it.day }.distinct()

            val meanHoursForTomorrow = weatherMean.filter { it.day == differentDays[0] }
            val meanHoursForDayAfterTomorrow = weatherMean.filter { it.day == differentDays[1] }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .padding(top = innerPadding.calculateTopPadding())
                ) {


                    item {
                        Column {
                            WeatherNow(weatherHours[0])

                            Row {
                                ElevatedButton(onClick = { navController.navigate("settings") }) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = "Location"
                                    )
                                    Text(
                                        text = location.shortName,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.padding(5.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                TodayForecastCard(allHours = allHours)
                                NextDaysForecastCard(weatherForDay = weatherDays[0], meanHours = meanHoursForTomorrow)
                                NextDaysForecastCard(weatherForDay = weatherDays[1], meanHours = meanHoursForDayAfterTomorrow)
                            }

                            Spacer(modifier = Modifier.padding(0.dp)) // + 10.dp from Arrangement.spaceBy
                        }

                    }
                }
            }
        }
    }
}


@SuppressLint("DiscouragedApi")
@Composable
fun WeatherNow(weather: GeneralForecast) {

    val context = LocalContext.current
    val drawableName = weather.symbol
    val drawableId =
        context.resources.getIdentifier(drawableName, "drawable", context.packageName) // need to use getIdentifier instead of R.drawable.. because of  the variable name.



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Image(painter = painterResource(id = drawableId), contentDescription = "weather now")

        Text(
            text = "${weather.temperature} °C",
            style = MaterialTheme.typography.displaySmall
        )


    }

}

@Composable
fun TodayForecastCard(allHours: List<GeneralForecast>) {

    val AMOUNT_SHOWN_EXPANDED = 12
    val AMOUNT_SHOW_HIDDEN = 3

    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {

        Column(
            modifier = Modifier
                .padding(20.dp)
                .animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)

            ) {
                val nextHoursTitle = if (isExpanded) "Neste $AMOUNT_SHOWN_EXPANDED timer" else "Neste $AMOUNT_SHOW_HIDDEN timer"
                Text(
                    text = nextHoursTitle,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            val amountOfHoursShown = if (isExpanded) 12 else 3

            Column(
                modifier = Modifier.animateContentSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                allHours.take(amountOfHoursShown).map { weather ->
                    SingleHourForecastCard(weather)
                }
            }


            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { isExpanded = !isExpanded }
            ) {

                val icon = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown
                val text = if (isExpanded) "Skjul" else "Se flere timer"

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = text, style = MaterialTheme.typography.labelLarge)
                    Icon(imageVector = icon, contentDescription = text)
                }
            }
        }
    }
}


@Composable
fun NextDaysForecastCard(weatherForDay: WeatherForDay, meanHours:List<WeatherForDay>){

    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
        ,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {

        Column(
            modifier = Modifier
                .padding(20.dp)
                .animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                val dayWithCapitalizedFirst = weatherForDay.day.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                Text(
                    text = dayWithCapitalizedFirst,
                    style = MaterialTheme.typography.titleLarge
                    ,
                )
            }

            WholeDayAverageWeatherCard(weatherForDay = weatherForDay)

            AnimatedVisibility(
                visible = isExpanded,
                enter = slideInVertically { -it / 2 } + fadeIn(),
                exit = fadeOut()

            ) {
                Column (
                ){
                    meanHours.map { weather ->
                        SixHourMeanForecastCard(weatherForDay = weather)
                    }
                }
            }

            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { isExpanded = !isExpanded }
            ) {

                val icon = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown
                val text = if (isExpanded) "Skjul" else "Se timevis"

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = text, style = MaterialTheme.typography.labelLarge)
                    Icon(imageVector = icon, contentDescription = text)
                }
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun SingleHourForecastCard(generalForecast: GeneralForecast) {


    val context = LocalContext.current
    val drawableName = generalForecast.symbol
    val drawableId =
        context.resources.getIdentifier(drawableName, "drawable", context.packageName) // need to use getIdentifier instead of R.drawable.. because of  the variable name.

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {


                Text(
                    text = generalForecast.hour + ":00",
                    style = MaterialTheme.typography.bodyMedium
                )
                Image(
                    painter = painterResource(id = drawableId),
                    contentDescription = "Værsymbol"
                )

                Text(
                    text = "${generalForecast.temperature}°C",
                    style = MaterialTheme.typography.bodyMedium
                )


                Text(
                    text = "${generalForecast.wind} m/s",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "${generalForecast.percipitation} mm",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }


}


@SuppressLint("DiscouragedApi")
@Composable
fun WholeDayAverageWeatherCard(weatherForDay: WeatherForDay) {


    //TODO find better way to showcase picture because of Discouraged API
    val context = LocalContext.current
    val drawableName = weatherForDay.symbol
    val drawableId =
        context.resources.getIdentifier(drawableName, "drawable", context.packageName)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "Værsymbol",
                modifier = Modifier.size(85.dp)
            )


            Spacer(modifier = Modifier.padding(10.dp))

            Column {
                Text(
                    text = "L: ${weatherForDay.lowestTemperature}°",
                    style = MaterialTheme.typography.titleMedium
                )



                Text(
                    text = "H: ${weatherForDay.highestTemperature}°",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

    }
}


@SuppressLint("DiscouragedApi")
@Composable
fun SixHourMeanForecastCard(weatherForDay: WeatherForDay) {


    val context = LocalContext.current
    val drawableName = weatherForDay.symbol
    val drawableId =
        context.resources.getIdentifier(drawableName, "drawable", context.packageName)

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            Text(
                text = "${weatherForDay.startingTime} - ${weatherForDay.endingTime}",
                style = MaterialTheme.typography.bodyMedium,
            )


            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "Værsymbol"
            )

            Text(
                text = "${weatherForDay.meanTemperature}°C",
                style = MaterialTheme.typography.bodyMedium,
            )


            Text(
                text = "${weatherForDay.wind} m/s",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = "${weatherForDay.precipitation} mm",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}





