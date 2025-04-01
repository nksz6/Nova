//ICS342 - NICK KELLEY

//package
package com.example.nova.ui.screens

//imports
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nova.R
import com.example.nova.data.model.WeatherResponse
import com.example.nova.ui.viewmodel.WeatherViewModel
import kotlin.math.roundToInt
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import com.example.nova.utils.getWeatherIcon

//current weather screen
@Composable
fun CurrentWeatherScreen(
    weatherData: WeatherResponse,
    viewModel: WeatherViewModel,
    navController: NavController
) {
    //state for zip-code input
    var zipCode by remember { mutableStateOf("") }
    var isZipCodeError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }



    //extract string resources at the composable level
    val zipCodeErrorText = stringResource(R.string.zip_code_error)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        //WEATHER CONTENT
        //condition image in the top right

        //grab the icon code
        val iconCode = weatherData.weather.firstOrNull()?.icon ?: "01d" //default to sunny if no icon


        Image(
            painter = painterResource(id = getWeatherIcon(iconCode)),
            contentDescription = stringResource(
                id = R.string.weather_condition,
                weatherData.weather.firstOrNull()?.main ?: ""
            ),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(vertical = 45.dp)
                .padding(horizontal = 25.dp)
                .size(120.dp)
        )

        //temperature - turns it into an int and displays as string in top left
        val currentTemp = weatherData.main.temp.roundToInt()
        Text(
            text = stringResource(id = R.string.current_temperature, currentTemp),
            fontSize = 75.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 50.dp)
                .padding(horizontal = 25.dp)
        )

        //location - it'll be at the top but not super big
        Text(
            text = stringResource(id = R.string.location, weatherData.name),
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 16.dp)
        )

        //feels-like - will be right under the current temp
        val feelsLikeTemp = weatherData.main.feelsLike.roundToInt()
        Text(
            text = stringResource(id = R.string.feels_like, feelsLikeTemp),
            fontSize = 25.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 140.dp)
                .padding(horizontal = 10.dp)
        )

        //low temp - will be relatively under the 'feels like'
        val lowTemp = weatherData.main.tempMin.roundToInt()
        Text(
            text = stringResource(id = R.string.low_temp, lowTemp),
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 225.dp)
                .padding(horizontal = 18.dp)
        )

        //high temp - under low temp
        val highTemp = weatherData.main.tempMax.roundToInt()
        Text(
            text = stringResource(id = R.string.high_temp, highTemp),
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 255.dp)
                .padding(horizontal = 18.dp)
        )

        //humidity
        val humidity = weatherData.main.humidity
        Text(
            text = stringResource(id = R.string.humidity_with_value, humidity),
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 285.dp)
                .padding(horizontal = 18.dp)
        )

        //pressure
        val pressure = weatherData.main.pressure
        Text(
            text = stringResource(id = R.string.pressure, pressure),
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 315.dp)
                .padding(horizontal = 18.dp)
        )

        //bottom section w/ zip-code input and buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //zip-code input
            OutlinedTextField(
                value = zipCode,
                onValueChange = {
                    //only allow digits and limit to 5 characters
                    if (it.all { char -> char.isDigit() } && it.length <= 5) {
                        zipCode = it
                        isZipCodeError = false
                    }
                },
                label = { Text(stringResource(R.string.enter_zip_code)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                isError = isZipCodeError,
                supportingText = {
                    if (isZipCodeError) {
                        Text(errorMessage)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(remember { FocusRequester() }) //use shared focus requester
            )

            Spacer(modifier = Modifier.height(8.dp))

            //Row w/ 'Get Weather' and 'View Forecast' buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                //get weather button
                Button(
                    onClick = {
                        if (zipCode.length == 5) {
                            //fetch weather for zip code
                            viewModel.fetchWeatherForZipCode(zipCode)
                            //also fetch forecast for the same zip
                            viewModel.fetchForecastForZipCode(zipCode)
                            isZipCodeError = false
                        } else {
                            isZipCodeError = true
                            errorMessage = zipCodeErrorText
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.get_weather))
                }

                Spacer(modifier = Modifier.width(8.dp))

                //view forecast button
                Button(
                    onClick = {
                        if (zipCode.length == 5) {
                            //fetch forecast before navigating
                            viewModel.fetchForecastForZipCode(zipCode)
                            //navigate to forecast screen
                            navController.navigate("forecast")
                        } else {
                            isZipCodeError = true
                            errorMessage = zipCodeErrorText
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.view_forecast))
                }
            }
        }
    }
}