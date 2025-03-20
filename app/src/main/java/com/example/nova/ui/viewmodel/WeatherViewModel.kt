//ICS342 NICK KELLEY
package com.example.nova.ui.viewmodel

//imports
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nova.data.model.WeatherResponse
import com.example.nova.data.network.WeatherApiService
import com.example.nova.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date


//ViewModel will manage the weather data state for the UI
//fetch the data from the repository and then expose the state via LiveData
class WeatherViewModel(
    //constructor injection for testin
    private val repository: WeatherRepository = WeatherRepository(WeatherApiService.create())
) : ViewModel() {

    companion object {
        private const val TAG = "WeatherViewModel"
        private const val DEFAULT_LOCATION = "Minnetrista,MN,US"

        //cache times out after 30 mins
        private const val CACHE_TIMEOUT_MS = 30 * 60 * 1000L

        //only ONE fetch per second
        private const val FETCH_DEBOUNCE_MS = 1000L
    }

    //livedata for the UI state
    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    //cache variables
    private var lastFetchTime: Long = 0
    private var lastLocation: String? = null

    //for rate limiting
    private var currentFetchJob: Job? = null

    //initial load for a default location
    init {
        fetchWeatherForLocation(DEFAULT_LOCATION, LocationType.CITY)
    }

    //enum to specify the type of location were getting since it was ambiguous
    enum class LocationType {
        CITY, ZIP_CODE, COORDINATES
    }

    //this just improves the type safety for coordinates
    data class Coordinates(val lat: Double, val lon: Double)

    //fetch weather data for a city name like I hardcoded
    fun fetchWeatherForCity(city: String) {
        fetchWeatherForLocation(city, LocationType.CITY)
    }

    //these two functions I left in because they were mentioned, I wanted to wait and see whether or I'd need them.
    fun fetchWeatherForZipCode(zipCode: String) {
        fetchWeatherForLocation(zipCode, LocationType.ZIP_CODE)
    }

    fun fetchWeatherForCoordinates(lat: Double, lon: Double) {
        fetchWeatherForLocation(
            Coordinates(lat, lon),
            LocationType.COORDINATES
        )
    }

     //common method to fetch weather data for any location type.
     //handles loading state, caching, error handling, and rate limiting.
    private fun fetchWeatherForLocation(location: Any, locationType: LocationType) {
        //cancel any ongoing fetch
        currentFetchJob?.cancel()

        //format location as string for cache check
        val locationKey = when (location) {
            is Coordinates -> "lat=${location.lat}&lon=${location.lon}"
            else -> location.toString()
        }

        //check if we should use cached data
        val currentTime = Date().time
        if (locationKey == lastLocation &&
            (currentTime - lastFetchTime) < CACHE_TIMEOUT_MS &&
            _weatherData.value != null
        ) {
            //cache is valid... don't fetch again
            Log.d(TAG, "Using cached data for $locationKey")
            return
        }

        //set loading state
        _isLoading.value = true
        _error.value = null

        // Store location for cache
        lastLocation = locationKey

        currentFetchJob = viewModelScope.launch {
            // Rate limiting - wait a bit if we recently made a request
            if (currentTime - lastFetchTime < FETCH_DEBOUNCE_MS) {
                delay(FETCH_DEBOUNCE_MS)
            }

            try {
                //call the right repository method depending on what got passed
                val result = when (locationType) {
                    LocationType.CITY -> repository.getWeatherByCity(location as String)
                    LocationType.ZIP_CODE -> repository.getWeatherByZip(location as String)
                    LocationType.COORDINATES -> {
                        val coords = location as Coordinates
                        repository.getWeatherByCoordinates(coords.lat, coords.lon)
                    }
                }

                //handle the result
                result.onSuccess { weather ->
                    Log.d(TAG, "Successfully fetched weather for $locationKey")
                    _weatherData.value = weather
                    _isLoading.value = false
                    lastFetchTime = Date().time

                    //debugging the temp values
                    Log.d(TAG, "Temp: ${weather.main.temp}, Min: ${weather.main.tempMin}, Max: ${weather.main.tempMax}")
                }.onFailure { exception ->
                    handleError(exception, locationKey)
                }
            } catch (e: Exception) {
                handleError(e, locationKey)
            }
        }
    }

    //helper method to handle errors
    private fun handleError(exception: Throwable, location: String) {
        Log.e(TAG, "Error fetching weather for $location: ${exception.message}", exception)

        //made error messages easier to understand
        val errorMessage = when (exception) {
            is java.net.UnknownHostException -> "No internet connection. Please check your network."
            is java.net.SocketTimeoutException -> "Connection timed out. Please try again later."
            is retrofit2.HttpException -> {
                when (exception.code()) {
                    401 -> "Authentication error. Please check API key."
                    404 -> "Location not found: $location"
                    429 -> "Too many requests. Please try again later."
                    else -> "Server error: ${exception.code()}. Please try again later."
                }
            }
            else -> exception.message ?: "Unknown error occurred"
        }

        _error.value = errorMessage
        _isLoading.value = false
    }
}