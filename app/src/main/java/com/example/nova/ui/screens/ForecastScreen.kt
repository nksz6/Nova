//ICS342 - NICK KELLEY

//package
package com.example.nova.ui.screens
//imports
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nova.R
import com.example.nova.data.model.DailyForecast
import com.example.nova.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

//forecast screen
@Composable
fun ForecastScreen(
    viewModel: WeatherViewModel,
    navController: NavController
) {
    //observe forecast data
    val forecastData by viewModel.forecastData.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(initial = true)
    val error by viewModel.error.observeAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        //back button at the top
        Button(
            onClick = { navController.navigateUp() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(stringResource(R.string.back_to_current))
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = stringResource(id = R.string.error_loading_forecast))
                        error?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Button(onClick = { navController.navigateUp() }) {
                            Text(stringResource(R.string.back_to_current))
                        }
                    }
                }
            }
            forecastData != null -> {
                //city name and header
                forecastData?.city?.let { city ->
                    Text(
                        text = stringResource(R.string.forecast_for, city.name),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                //forecast list
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    forecastData?.list?.let { forecasts ->
                        items(forecasts) { dailyForecast ->
                            ForecastDayItem(dailyForecast)
                            HorizontalDivider()
                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_forecast_data))
                }
            }
        }
    }
}

@Composable
fun ForecastDayItem(forecast: DailyForecast) {
    val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    val date = Date(forecast.dt * 1000) // Convert from Unix timestamp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            //date
            Text(
                text = dateFormat.format(date),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            //temperature range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(
                        R.string.high_temp,
                        forecast.temp.max.roundToInt()
                    )
                )
                Text(
                    text = stringResource(
                        R.string.low_temp,
                        forecast.temp.min.roundToInt()
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            //weather description
            forecast.weather.firstOrNull()?.let { weather ->
                Text(
                    text = weather.description.capitalize(),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            //additional details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(
                        R.string.humidity_with_value,
                        forecast.humidity
                    )
                )
                Text(
                    text = stringResource(
                        R.string.wind_speed,
                        forecast.windSpeed
                    )
                )
            }
        }
    }
}

//extension function to capitalize first letter of a string
fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}