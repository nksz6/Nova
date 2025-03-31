//ICS342 NICK KELLEY

package com.example.nova //package

//basic android imports
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge //displays stuff edge to edge
import androidx.activity.viewModels //lets me get ViewModel instances

//navigation imports
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


//Jetpack Compose imports

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState //converts LiveData to compose
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


//project imports
import com.example.nova.ui.theme.NovaTheme
import androidx.compose.ui.graphics.Color
import com.example.nova.ui.theme.Brown
import com.example.nova.ui.viewmodel.WeatherViewModel
import com.example.nova.ui.screens.CurrentWeatherScreen
import com.example.nova.ui.screens.ForecastScreen


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
                //setup navigation
                val navController = rememberNavController()

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

                    //Navigation host
                    NavHost(
                        navController = navController,
                        startDestination = "currentWeather",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        //weather screen
                        composable("currentWeather") {
                            val weatherData by viewModel.weatherData.observeAsState()
                            val isLoading by viewModel.isLoading.observeAsState(initial = true)
                            val error by viewModel.error.observeAsState()

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

                                weatherData != null -> {
                                    CurrentWeatherScreen(
                                        weatherData = weatherData!!,
                                        viewModel = viewModel,
                                        navController = navController
                                    )
                                }

                                else -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No weather data available")
                                    }
                                }
                            }
                        }

                        //forecast screen
                        composable("forecast") {
                            ForecastScreen(
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}