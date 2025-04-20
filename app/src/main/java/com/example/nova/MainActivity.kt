//ICS342 NICK KELLEY

package com.example.nova

//android imports
import android.content.ComponentName

import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
//navigation imports
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//Jetpack Compose imports
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
//project imports
import com.example.nova.ui.theme.NovaTheme
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.nova.ui.theme.Brown
import com.example.nova.ui.theme.White
import com.example.nova.ui.theme.MochiPopOne
import com.example.nova.ui.viewmodel.WeatherViewModel
import com.example.nova.ui.screens.CurrentWeatherScreen
import com.example.nova.ui.screens.ForecastScreen
import com.example.nova.data.network.WeatherApiService
import com.example.nova.data.repository.WeatherRepository
import com.example.nova.services.LocationService
import com.example.nova.data.model.LocationWeatherViewModel

//MainActivity declaration, creating ViewModel instance...
class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()

    //LocationWeatherViewModel & Service connection
    private lateinit var locationWeatherViewModel: LocationWeatherViewModel
    private var locationService: LocationService? = null
    private var isBound = false

    //Service connection for binding to location service
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocationBinder
            locationService = binder.getService()
            isBound = true

            //initializing service w/ repository and location listener
            locationService?.setWeatherRepository(WeatherRepository(WeatherApiService.create()))
            locationService?.setLocationListener(object : LocationService.LocationListener {
                override fun onNewLocation(location: Location) {
                    //when getting a new location, fetch the weather for it
                    locationWeatherViewModel.fetchWeatherForLocation(location)
                }
            })

            //if permissions are already granted, request location
            if (hasLocationPermission()) {
                requestLocation()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            isBound = false
        }
    }

    //REQUEST LAUNCHERS
    //location permission launcher
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            //if location permission granted, also check notification permission
            if (!hasNotificationPermission()) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                startLocationService()
            }
        } else {
            //permission denied
            Toast.makeText(
                this,
                getString(R.string.location_permission_required),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //notification permission launcher
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startLocationService()
        } else {
            //show message but continue...(graceful handling)
            Toast.makeText(
                this,
                getString(R.string.notification_permission_required),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //onCreate + enable edge to edge
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initialize LocationWeatherViewModel
        locationWeatherViewModel =
            LocationWeatherViewModel(WeatherRepository(WeatherApiService.create()))

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
                            color = Brown,
                            tonalElevation = 10.dp
                        ) {
                            //setting up textbox
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 25.dp)
                                    .padding(start = 10.dp)
                            ) {
                                //title the top left
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = MochiPopOne,
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(top = 25.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    //Navigation/NavHost
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
                                            Text(
                                                text = stringResource(id = R.string.error_loading_weather),
                                                fontFamily = MochiPopOne
                                            )
                                            error?.let {
                                                Text(
                                                    text = it,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.padding(16.dp),
                                                    fontFamily = MochiPopOne
                                                )
                                            }
                                            Button(
                                                onClick = {
                                                    viewModel.resetToDefaultLocation() //when theres an error, reset to the default location
                                                },
                                                shape = MaterialTheme.shapes.small,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Brown,
                                                    contentColor = White
                                                ),
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                Text(
                                                    stringResource(R.string.reset_location),
                                                    fontFamily = MochiPopOne
                                                )
                                            }
                                        }
                                    }
                                }

                                weatherData != null -> {
                                    CurrentWeatherScreen(
                                        weatherData = weatherData!!,
                                        viewModel = viewModel,
                                        navController = navController,
                                        onMyLocationClicked = { onMyLocationClicked() }
                                    )
                                }

                                else -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "No weather data available",
                                            fontFamily = MochiPopOne
                                        )
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

    override fun onStart() {
        super.onStart()
        //bind to service
        if (!isBound) {
            Intent(this, LocationService::class.java).also { intent ->
                bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        //unbind from service
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    //handler for location button click
    private fun onMyLocationClicked() {
        //request permission if not granted
        if (!hasLocationPermission()) {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            //request notification permission
            if (!hasNotificationPermission()) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                //start service and request location
                startLocationService()
            }
        }
    }

    //check if location permission is granted
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    //check if notification permission is granted
    private fun hasNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Start the location service
    private fun startLocationService() {
        // Start the service
        val serviceIntent = Intent(this, LocationService::class.java)
        startForegroundService(serviceIntent)
        //req location update
        requestLocation()
    }

    //request location from the service
    private fun requestLocation() {
        locationService?.getLastLocation { location ->
            if (location != null) {
                locationWeatherViewModel.fetchWeatherForLocation(location)
            } else {
                // Start location updates if no last location
                locationService?.startLocationUpdates()
            }
        }
    }

    private fun observeLocationWeather() {
        locationWeatherViewModel.locationWeatherData.observe(this) { weatherData ->

            //update UI with locationBased weather data
            Log.d("MainActivity", "Location weather updated: ${weatherData?.name}")

            locationWeatherViewModel.isLoading.observe(this) { isLoading ->
                //show/hide loading for location-based updates
            }

            locationWeatherViewModel.error.observe(this) { error ->
                if (error != null) {
                    Toast.makeText(
                        this, "Location weather error: $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }
}