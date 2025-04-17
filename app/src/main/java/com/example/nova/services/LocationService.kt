//ICS342 - NICK KELLEY
package com.example.nova.services

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.nova.MainActivity
import com.example.nova.R
import com.example.nova.data.model.WeatherResponse
import com.example.nova.data.repository.WeatherRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.*


class LocationService : Service() {

    companion object {
        private const val TAG = "LocationService"
        private const val NOTIFICATION_CHANNEL_ID = "weather_notification_channel"
        private const val NOTIFICATION_ID = 1001
        private const val LOCATION_REQUEST_INTERVAL = 10 * 60 * 1000L // 10 minutes
        private const val MIN_UPDATE_INTERVAL = 1 * 60 * 1000L
    }

    //Binder given to clients
    private val binder = LocationBinder()

    //Location client
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    //Repository for weather
    private lateinit var weatherRepository: WeatherRepository

    //Coroutine scope
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    //Notification data
    private var currentWeather: WeatherResponse? = null

    //for client binding
    inner class LocationBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    //interface for location callbacks to the activity/viewmodel
    interface LocationListener {
        fun onNewLocation(location: Location)
    }

    //client callback
    private var locationClientListener: LocationListener? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize location callback - use google LocationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d(TAG, "New location: ${location.latitude}, ${location.longitude}")
                    // Notify client of the new location
                    locationClientListener?.onNewLocation(location)
                    // Update weather for notification
                    fetchWeatherForNotification(location)
                }
            }
        }
        // Create notification channel
        createNotificationChannel()
    }

    //procedure for starting
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand")
        // Initial notification while we fetch location
        val initialNotification = createNotification(
            getString(R.string.app_name),
            "Fetching location..." //shows while its loading the location
        )
        // Start as foreground service
        startForeground(
            NOTIFICATION_ID,
            initialNotification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        )
        startLocationUpdates() //starts updates when the service is started
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        serviceJob.cancel()
    }

    //start location updates
    fun startLocationUpdates() {
        try {
            //create a location req using the builder API
            val locationRequest = LocationRequest.Builder(LOCATION_REQUEST_INTERVAL)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY) //changed from balanced to high bc of issue
                .setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            Log.d(TAG, "Started location updates")
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception when requesting location updates: ${e.message}")
        }
    }

    //stop location updates
    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d(TAG, "Stopped location updates.")
    }

    //fetching weather for the notification
    private fun fetchWeatherForNotification(location: Location) {
        if (!::weatherRepository.isInitialized) {
            Log.e(TAG, "Weather Repository is not initialized.")
            return
        }

        serviceScope.launch {
            try {
                val result = weatherRepository.getWeatherByCoordinates(
                    location.latitude,
                    location.longitude
                )

                result.onSuccess { weatherResponse ->
                    Log.d(TAG, "Weather fetched for notification: ${weatherResponse.name}")
                    currentWeather = weatherResponse
                    updateNotification(weatherResponse)
                }.onFailure { exception ->
                    Log.e(TAG, "Error fetching weather for notification: ${exception.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching weather: ${e.message}")
            }
        }
    }

    //creation of notification channel
    private fun createNotificationChannel() {
        val name = getString(R.string.weather_notification_channel_name)
        val description = getString(R.string.weather_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            this.description = description
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    //create notification
    private fun createNotification(title: String, content: String): Notification {
        //'intent' is to open app when notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        //Build and return notification
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_sunny) //using existing sun icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    //update notification with weather data
    private fun updateNotification(weather: WeatherResponse) {
        val title = weather.name
        val temp = weather.main.temp.toInt()
        val condition = weather.weather.firstOrNull()?.main ?: "Unknown"

        val content = "$temp° - $condition"
        val notification = createNotification(title, content)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    //set client listener
    fun setLocationListener(listener: LocationListener) {
        this.locationClientListener = listener
    }

    //set the weather repository (injected from ViewModel)
    fun setWeatherRepository(repository: WeatherRepository) {
        this.weatherRepository = repository
    }

//    // Get last known location (one-time request)
//    fun getLastLocation(callback: (Location?) -> Unit) {
//        try {
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location ->
//                    if (location != null) {
//                        Log.d(TAG, "Last location: ${location.latitude}, ${location.longitude}")
//                        callback(location)
//
//                        // Also fetch weather for notification
//                        fetchWeatherForNotification(location)
//                    } else {
//                        Log.d(TAG, "Last location is null, requesting updates")
//                        callback(null)
//                        // Start updates to get location
//                        startLocationUpdates()
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.e(TAG, "Error getting last location: ${e.message}")
//                    callback(null)
//                }
//        } catch (e: SecurityException) {
//            Log.e(TAG, "Security exception when getting last location: ${e.message}")
//            callback(null)
//        }
//    }

}