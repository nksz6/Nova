//ICS342 - NICK KELLEY
package com.example.nova.data.model

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nova.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class LocationWeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _locationWeatherData = MutableLiveData<WeatherResponse?>()
    val locationWeatherData: LiveData<WeatherResponse?> = _locationWeatherData

    //fetch weather for the location
    fun fetchWeatherForLocation(location: Location) {
        viewModelScope.launch {
            try {
                val result = repository.getWeatherByCoordinates(
                    location.latitude,
                    location.longitude
                )

                result.onSuccess { weatherResponse ->
                    Log.d(
                        "LocationWeatherViewModel",
                        "Weather data fetched successfully for location: ${weatherResponse.name}"
                    )
                    _locationWeatherData.value = weatherResponse
                }.onFailure { exception ->
                    Log.e(
                        "LocationWeatherViewModel",
                        "Exception while fetching weather for location: ${exception.message}"
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    "LocationWeatherViewModel",
                    "Error fetching weather for location: ${e.message}"
                )
            }
        }
    }

    fun clearCache() {
        _locationWeatherData.value = null
    }
}









//    //enable/disable location based weather
//    fun setLocationEnabled(enabled: Boolean) {
//        _isLocationEnabled.value = enabled
//        if (!enabled) {
//            //clear location data when disabled
//            _locationWeatherData.value = null
//        }
//    }
//
//
//    //helper method for error handling
//    private fun handleError(exception: Throwable) {
//        Log.e(TAG, "Error fetching weather for location: ${exception.message}", exception)
//
//        val errorMessage = when (exception) {
//            is java.net.UnknownHostException -> "No Internet Connection. Please check your network."
//            is java.net.SocketTimeoutException -> "Connection timed out. Please try again later."
//            is retrofit2.HttpException -> {
//                when (exception.code()) {
//                    401 -> "Authentication error. Please check your API key."
//                    404 -> "Location not found."
//                    429 -> "Too many requests. Please try again later."
//                    else -> "Server error: ${exception.code()}. Please try again later."
//                }
//            }
//            else -> exception.message ?: "Unknown error occurred."
//        }
//        _error.value = errorMessage
//        _isLoading.value = false
//    }
//
//
//
//    //check method to see if cache is valid
//    private fun isCacheValid(newLocation: Location): Boolean {
//        val currentTime = Date().time
//        val lastLoc = lastLocation
//
//        return lastLoc != null &&
//                (_locationWeatherData.value != null) &&
//                (currentTime - lastLocationFetchTime) < CACHE_TIMEOUT_MS &&
//                (lastLoc.distanceTo(newLocation) < 1000)
//
//    }
//
//
//
//
//
//
//
//}