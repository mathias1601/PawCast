package no.uio.ifi.in2000.team19.prosjekt.ui.weather

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.WeatherForDay
import no.uio.ifi.in2000.team19.prosjekt.ui.home.NoConnectionScreen


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


@SuppressLint("DiscouragedApi")
@Composable
fun WeatherForecastCard(generalForecast: GeneralForecast) {

    val newColor = Color(0xffece9e4)

    val context = LocalContext.current
    val drawableName = generalForecast.symbol
    val drawableId =
        context.resources.getIdentifier(drawableName, "drawable", context.packageName)

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
                text = generalForecast.hour,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}



@SuppressLint("DiscouragedApi")
@Composable
fun WeatherForecastCardForDays(weatherForDay: WeatherForDay) {


        val newColor = Color(0xffece9e4)

        //TODO find better way to showcase picture because of Discouraged API
        val context = LocalContext.current
        val drawableName = weatherForDay.symbol
        val drawableId =
            context.resources.getIdentifier(drawableName, "drawable", context.packageName)

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
                    text = "${weatherForDay.lowestTemperature}°C",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.size(35.dp))

                Text(
                    text = "${weatherForDay.highestTemperature}°C",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.size(35.dp))

                Text(
                    text = weatherForDay.day,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }



@SuppressLint("DiscouragedApi")
@Composable
fun WeatherForecastMean(weatherForDay: WeatherForDay) {


        val newColor = Color(0xffece9e4)

        //TODO find better way to showcase picture because of Discouraged API
        val context = LocalContext.current
        val drawableName = weatherForDay.symbol
        val drawableId =
            context.resources.getIdentifier(drawableName, "drawable", context.packageName)

        Card(
            colors = CardDefaults.cardColors(
                containerColor = newColor
            ),
            modifier = Modifier
                .size(width = 350.dp, height = 75.dp)
                .padding(9.dp)
            //.height(23.dp)
        ) {

            Column {

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
                        text = "${weatherForDay.startingTime} - ${weatherForDay.endingTime}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.size(35.dp))

                    Text(
                        text = "${weatherForDay.meanTemperature}°C",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.size(35.dp))
                }

                Text(
                    text = "${weatherForDay.startingTime}°C",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "${weatherForDay.endingTime}°C",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )

                    /*Text(
                    text = weatherForDay.day,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )

                 */
            }
        }
    }





