//ICS342 NICK KELLEY

package com.example.nova //package

//basic android imports
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge //displays stuff edge to edge
import androidx.activity.viewModels //lets me get ViewModel instances

//Jetpack Compose imports
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState //converts LiveData to compose
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//project imports
import com.example.nova.data.model.WeatherResponse
import com.example.nova.ui.theme.NovaTheme
import androidx.compose.ui.graphics.Color
import com.example.nova.ui.theme.Brown
import com.example.nova.ui.viewmodel.WeatherViewModel
import kotlin.math.roundToInt //decimals to ints


//declare MainActivity & create a ViewModel instance
class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()
    //onCreate + enable edge to edge
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //define the compose UI stuff and apply the theme
        setContent {
            NovaTheme {
                //Scaffold
                Scaffold(
                    //top bar here
                    topBar = {
                        //my custom bar
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Brown, //personal choice
                            tonalElevation = 10.dp
                        ) {
                            //setting up textbox
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 25.dp)
                                    .padding(start = 10.dp)
                            ) {
                                //this make it say "Nova"
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(top = 12.5.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    //observe the weather data
                    val weatherData by viewModel.weatherData.observeAsState() //fetching weather data
                    val isLoading by viewModel.isLoading.observeAsState(initial = true) //loading while its fetching the data
                    val error by viewModel.error.observeAsState() //error message or null if no error

                    when {
                        //if its loading it will spin lol
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                contentAlignment = Alignment.Center
                            ) {
                                //this is the spinner
                                CircularProgressIndicator()
                            }
                        }
                        //if theres an error it'll show an error message and you can retry
                        error != null -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = stringResource(id = R.string.error_loading_weather))
                                    error?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                    Button(onClick = {
                                        viewModel.fetchWeatherForCity("Minnetrista,MN,US")
                                    }) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                        //if theres not an error though, show WeatherScreen composable
                        weatherData != null -> {
                            WeatherScreen(
                                weatherData = weatherData!!,
                                modifier = Modifier.padding(innerPadding) //remember padding
                            )
                        }
                        //if it can't fetch the weather and theres not an error it'll just say so.
                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No weather data available")
                            }
                        }
                    }
                }
            }
        }
    }
}

//so seperate composable to display the weather
@Composable
fun WeatherScreen(weatherData: WeatherResponse, modifier: Modifier = Modifier) { //takes the weather response and optional modifier
    //will use a box
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        //here is my Sunny image in the top right
        Image(
            painter = painterResource(id = R.drawable.ic_sunny),
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

        //Temperature - turns it into an int and displays as string in top left
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

        //Location - it'll be at the top but not super big
        Text(
            text = stringResource(id = R.string.location, weatherData.name),
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 16.dp)
        )

        //Feels like - will be right under the current temp
        val feelsLikeTemp = weatherData.main.feelsLike.roundToInt()
        Text(
            text = stringResource(id = R.string.feels_like, feelsLikeTemp),
            fontSize = 25.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 140.dp)
                .padding(horizontal = 10.dp)
        )

        //Low Temp - will be relatively under the 'feels like'
        val lowTemp = weatherData.main.tempMin.roundToInt()
        Text(
            text = stringResource(id = R.string.low_temp, lowTemp),
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 225.dp)
                .padding(horizontal = 18.dp)
        )

        //High Temp - under Low Temp
        val highTemp = weatherData.main.tempMax.roundToInt()
        Text(
            text = stringResource(id = R.string.high_temp, highTemp),
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 255.dp)
                .padding(horizontal = 18.dp)
        )

        //Humidity - had an issue with string form so just concatenating
        val humidity = weatherData.main.humidity
        Text(
            text = "${stringResource(id = R.string.humidity_prefix)} $humidity${stringResource(id = R.string.humidity_unit)}",
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 285.dp)
                .padding(horizontal = 18.dp)
        )

        //Pressure - under humidity and the last one for now, still relatively in the middle of the screen
        val pressure = weatherData.main.pressure
        Text(
            text = stringResource(id = R.string.pressure, pressure),
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 315.dp)
                .padding(horizontal = 18.dp)
        )
    }
}