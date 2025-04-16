//ICS342 - NICK KELLEY
package com.example.nova.data.model

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nova.data.model.WeatherResponse //to be implemented
import com.example.nova.data.repository.WeatherRepository
import kotlinx.coroutines.launch
import java.util.*

class LocationWeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LocationWeatherViewModel"
        private const val CACHE_TIMEOUT_MS = (30 * 60 * 1000L) //update every 30 minutes
    }

    //LiveData for UI
    private val _locationWeatherData = MutableLiveData<WeatherResponse?>()
    val locationWeatherData: LiveData<WeatherResponse?> = _locationWeatherData

    private val _isLocationEnabled = MutableLiveData<Boolean>(false)
    val isLocationEnabled: LiveData<Boolean> = _isLocationEnabled

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    //cache variables
    private var lastLocationFetchTime: Long = 0
    private var lastLocation: Location? = null

    //fetches weather for location
    fun fetchWeatherForLocation(location: Location) {
        //first check the cache
        if (isCacheValid(location)) {
            Log.d(TAG, "Using cached weather data.")
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = repository.getWeatherByCoordinates(
                    location.latitude,
                    location.longitude
                )

                result.onSuccess { weatherResponse ->
                    Log.d(TAG, "Successfully fetched weather for location: ${weatherResponse.name}")
                    _locationWeatherData.value = weatherResponse
                    _isLoading.value = false
                    _isLocationEnabled.value = true

                    //update the cache nao
                    lastLocationFetchTime = Date().time
                    lastLocation = location

                }.onFailure { exception ->
                    handleError(exception)
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    //enable/disable location based weather
    fun setLocationEnabled(enabled: Boolean) {
        _isLocationEnabled.value = enabled
        if (!enabled) {
            //clear location data when disabled
            _locationWeatherData.value = null
        }
    }


    //helper method for error handling
    private fun handleError(exception: Throwable) {
        Log.e(TAG, "Error fetching weather for location: ${exception.message}", exception)

        val errorMessage = when (exception) {
            is java.net.UnknownHostException -> "No Internet Connection. Please check your network."
            is java.net.SocketTimeoutException -> "Connection timed out. Please try again later."
            is retrofit2.HttpException -> {
                when (exception.code()) {
                    401 -> "Authentication error. Please check your API key."
                    404 -> "Location not found."
                    429 -> "Too many requests. Please try again later."
                    else -> "Server error: ${exception.code()}. Please try again later."
                }
            }
            else -> exception.message ?: "Unknown error occurred."
        }
        _error.value = errorMessage
        _isLoading.value = false
    }



    //check method to see if cache is valid
    private fun isCacheValid(newLocation: Location): Boolean {
        val currentTime = Date().time
        val lastLoc = lastLocation

        return lastLoc != null &&
                (_locationWeatherData.value != null) &&
                (currentTime - lastLocationFetchTime) < CACHE_TIMEOUT_MS &&
                (lastLoc.distanceTo(newLocation) < 1000)

    }







}