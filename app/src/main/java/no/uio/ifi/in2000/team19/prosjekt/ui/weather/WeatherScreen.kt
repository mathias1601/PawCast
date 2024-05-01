package no.uio.ifi.in2000.team19.prosjekt.ui.weather

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay
import no.uio.ifi.in2000.team19.prosjekt.ui.home.NoConnectionScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(weatherScreenViewModel: WeatherScreenViewModel) {

    var todayExpanded by remember { mutableStateOf(false) }
    var tomorrowExpanded by remember { mutableStateOf(false) }
    var dayAfterTomorrowExpanded by remember { mutableStateOf(false) }

    //val newColor = Color(0xffece9e4)
    val color = Color(0xffbfebfa)
    val colorForTitle = Color(0xff94ddf7)


    when (val weatherUiState = weatherScreenViewModel.weatherUiState.collectAsState().value) {
        is WeatherUiState.Loading -> CircularProgressIndicator()
        is WeatherUiState.Error -> NoConnectionScreen()
        is WeatherUiState.Success -> {
            val weatherHours = weatherUiState.weather.general
            val weatherDays = weatherUiState.weather.day
            val weatherMean = weatherUiState.weather.hours

            var firstHours: List<GeneralForecast>

            if (weatherHours.size > 3) {
                val indices = listOf(1, 2, 3) // Definerer hvilke indekser du vil inkludere
                firstHours = weatherHours.slice(indices)
            }
            else {
                firstHours = weatherHours
            }
            val allHours = weatherHours.drop(1)

            val differentDays = weatherDays.map { it.day }.distinct()

            val meanHoursForTomorrow = weatherMean.filter { it.day == differentDays[0] }
            val meanHoursForDayAfterTomorrow = weatherMean.filter { it.day == differentDays[1] }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xffdff5fd)),
                contentAlignment = Alignment.Center
            ) {

                LazyColumn (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)) {

                    item {
                        Spacer(modifier = Modifier.size(20.dp))
                            Text(
                                text = "Værvarsel",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                .fillMaxWidth(), // Fyller maksimal bredde. // Sørger for at teksten er sentrert horisontalt.
                                textAlign = TextAlign.Center
                                //modifier = Modifier.align(Alignment.Center)
                            )
                        Spacer(modifier = Modifier.size(20.dp))
                    }


                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .animateContentSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorForTitle
                            )
                            //elevation = 4.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { todayExpanded = !todayExpanded }
                                    .padding(16.dp)) {
                                //val dayWithCapitalizedFirst = weatherDays[0].day.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                //Text(text = dayWithCapitalizedFirst)
                                Text(
                                    text = "I dag",
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.weight(12f))
                                Icon(
                                    imageVector = if (todayExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                    contentDescription = if (todayExpanded) "Collapse" else "Expand",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.size(6.dp))
                        }
                    }


                    if (!todayExpanded) {
                        items(firstHours) { weather ->
                            WeatherForecastCard(weather, color)
                        }
                    }


                    if (todayExpanded) {
                        items(allHours) { weather ->
                            WeatherForecastCard(weather, color)
                        }
                    }



                    item {
                        Spacer(modifier = Modifier.size(10.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .animateContentSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorForTitle
                            )
                            //elevation = 4.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { tomorrowExpanded = !tomorrowExpanded }
                                    .padding(16.dp)) {
                                val dayWithCapitalizedFirst =
                                    weatherDays[0].day.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                Text(
                                    text = dayWithCapitalizedFirst,
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.weight(12f))
                                Icon(
                                    imageVector = if (tomorrowExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                    contentDescription = if (tomorrowExpanded) "Collapse" else "Expand",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.size(6.dp))
                        }
                    }

                    if (!tomorrowExpanded) {
                        item {
                            WeatherForecastCardForDays(weatherForDay = weatherDays[0], color)
                        }
                    }

                    if (tomorrowExpanded) {
                        items(meanHoursForTomorrow) { weather ->
                            WeatherForecastMean(weatherForDay = weather, color)
                        }

                    }



                    item {
                        Spacer(modifier = Modifier.size(10.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .animateContentSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorForTitle
                            )
                            //elevation = 4.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        dayAfterTomorrowExpanded = !dayAfterTomorrowExpanded
                                    }
                                    .padding(16.dp)) {
                                val dayWithCapitalizedFirst =
                                    weatherDays[1].day.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                Text(
                                    text = dayWithCapitalizedFirst,
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.weight(12f))
                                Icon(
                                    imageVector = if (dayAfterTomorrowExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                    contentDescription = if (dayAfterTomorrowExpanded) "Collapse" else "Expand",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.size(6.dp))
                        }
                    }

                    if (!dayAfterTomorrowExpanded) {
                        item {
                            WeatherForecastCardForDays(weatherForDay = weatherDays[1], color)
                        }
                    }

                    if (dayAfterTomorrowExpanded) {
                        items(meanHoursForDayAfterTomorrow) { weather ->
                            WeatherForecastMean(weatherForDay = weather, color)
                        }
                    }


                }


            }
        }
    }
}

/*
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(weatherScreenViewModel: WeatherScreenViewModel) {

    when (val weatherUiState = weatherScreenViewModel.weatherUiState.collectAsState().value) {
        is WeatherUiState.Loading -> CircularProgressIndicator()
        is WeatherUiState.Error -> NoConnectionScreen()
        is WeatherUiState.Success -> {
            val weatherHours = weatherUiState.weather.general
            val weatherDays = weatherUiState.weather.day
            val weatherMean = weatherUiState.weather.hours

            LazyColumn {

                items(weatherHours) { generalForecast ->
                    WeatherForecastCard(generalForecast = generalForecast)
                }

                items(weatherDays) { weatherData ->
                    WeatherForecastCardForDays(weatherForDay = weatherData)
                }

                items(weatherMean) { weatherHoursMean ->
                    WeatherForecastMean(weatherForDay = weatherHoursMean)
                }
            }
        }
    }
}
*/



@SuppressLint("DiscouragedApi")
@Composable
fun WeatherForecastCard(generalForecast: GeneralForecast, color: Color) {


    val context = LocalContext.current
    val drawableName = generalForecast.symbol
    val drawableId =
        context.resources.getIdentifier(drawableName, "drawable", context.packageName)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            //.size(width = 350.dp, height = 75.dp)
            //.padding(9.dp)
        //.height(23.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize(),
            //horizontalArrangement = Arrangement.Center, // Horisontalt midtstille alle elementer i raden
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.size(15.dp))

            Text(
                text = generalForecast.hour + ":00",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.size(15.dp))

            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "Værsymbol"
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text(
                text = "${generalForecast.temperature}°C",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text(
                text = "${generalForecast.wind} m/s",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text(
                text = "${generalForecast.percipitation} mm",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}



@SuppressLint("DiscouragedApi")
@Composable
fun WeatherForecastCardForDays(weatherForDay: WeatherForDay, color: Color) {



        //TODO find better way to showcase picture because of Discouraged API
        val context = LocalContext.current
        val drawableName = weatherForDay.symbol
        val drawableId =
            context.resources.getIdentifier(drawableName, "drawable", context.packageName)

        Card(
            colors = CardDefaults.cardColors(
                containerColor = color
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
            //.height(23.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center, // Horisontalt midtstille alle elementer i raden
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(modifier = Modifier.size(15.dp))

                Image(
                    painter = painterResource(id = drawableId),
                    contentDescription = "Værsymbol",
                    modifier = Modifier.size(83.dp)
                )
                Spacer(modifier = Modifier.size(45.dp))

                Column {
                    Text(
                        text = "L: ${weatherForDay.lowestTemperature}°C",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Text(
                        text = "H: ${weatherForDay.highestTemperature}°C",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }


            }
        }
    }



@SuppressLint("DiscouragedApi")
@Composable
fun WeatherForecastMean(weatherForDay: WeatherForDay, color: Color) {


    val context = LocalContext.current
    val drawableName = weatherForDay.symbol
    val drawableId =
        context.resources.getIdentifier(drawableName, "drawable", context.packageName)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        //.size(width = 350.dp, height = 75.dp)
        //.padding(9.dp)
        //.height(23.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize(),
            //horizontalArrangement = Arrangement.Center, // Horisontalt midtstille alle elementer i raden
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.size(15.dp))

            Text(
                text = "${weatherForDay.startingTime} - ${weatherForDay.endingTime}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.size(15.dp))

            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "Værsymbol"
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text(
                text = "${weatherForDay.meanTemperature}°C",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.size(20.dp))

            /*Text(
                text = "${weatherForDay.wind} m/s",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text(
                text = "${generalForecast.percipitation} mm",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )

             */
        }
    }
}





