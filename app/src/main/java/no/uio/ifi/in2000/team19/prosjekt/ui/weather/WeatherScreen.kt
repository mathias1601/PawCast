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

            val allHours = weatherHours.drop(1)

            val next12Hours = allHours.subList(0, 12)
            val next3Hours = allHours.subList(0, 3)

            val differentDays = weatherDays.map { it.day }.distinct()

            val meanHoursForTomorrow = weatherMean.filter { it.day == differentDays[0] }
            val meanHoursForDayAfterTomorrow = weatherMean.filter { it.day == differentDays[1] }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xffdff5fd)),
                //contentAlignment = Alignment.BottomCenter
            ) {


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 121.dp)
                        .padding(top = 15.dp)
                ) {

                    item {
                        Spacer(modifier = Modifier.size(60.dp))
                        Text(
                            text = "Værvarsel",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth(), // Fyller maksimal bredde. // Sørger for at teksten er sentrert horisontalt.
                            textAlign = TextAlign.Center
                            //modifier = Modifier.align(Alignment.Center)
                        )

                    }

                    item {
                        Spacer(modifier = Modifier.size(150.dp))
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

                                if (!todayExpanded) {
                                    Text(
                                        text = "Neste 3 timer",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = "Neste 12 timer",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }


                                Spacer(Modifier.weight(12f))
                                Icon(
                                    imageVector = if (todayExpanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                                    contentDescription = if (todayExpanded) "Collapse" else "Expand",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            //Spacer(modifier = Modifier.size(6.dp))
                        }
                    }


                    if (!todayExpanded) {
                        items(next3Hours) { weather ->
                            WeatherForecastCard(weather, color)
                        }
                    }


                    if (todayExpanded) {
                        items(next12Hours) { weather ->
                            WeatherForecastCard(weather, color)
                        }
                    }



                    item {
                        //Spacer(modifier = Modifier.size(10.dp))

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
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.weight(12f))
                                Icon(
                                    imageVector = if (tomorrowExpanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                                    contentDescription = if (tomorrowExpanded) "Collapse" else "Expand",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
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
                        //Spacer(modifier = Modifier.size(10.dp))

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
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.weight(12f))
                                Icon(
                                    imageVector = if (dayAfterTomorrowExpanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                                    contentDescription = if (dayAfterTomorrowExpanded) "Collapse" else "Expand",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
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

    ) {

        Row(
            modifier = Modifier
                .fillMaxSize(),
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
                text = "${generalForecast.temperature}°",
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
            horizontalArrangement = Arrangement.Center,
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
                    text = "L: ${weatherForDay.lowestTemperature}°",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "H: ${weatherForDay.highestTemperature}°",
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

            Text(
                text = "${weatherForDay.wind} m/s",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text(
                text = "${weatherForDay.precipitation} mm",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}





