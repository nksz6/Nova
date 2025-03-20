//ICS342 NICK KELLEY

package com.example.nova.data.repository

//imports
import android.util.Log
import com.example.nova.data.model.WeatherResponse
import com.example.nova.data.network.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

//this repository class is gonna handle data operations
//basically the 'middleman' between the API and ViewModel
class WeatherRepository(private val weatherApiService: WeatherApiService) {

    companion object {
        private const val TAG = "WeatherRepository"
    }

    //getting weather by city
    suspend fun getWeatherByCity(city: String): Result<WeatherResponse> {
        return fetchWeather {
            Log.d(TAG, "Fetching weather for city: $city")
            weatherApiService.getCurrentWeather(city)
        }
    }

    //getting weather by zip
    suspend fun getWeatherByZip(zipCode: String): Result<WeatherResponse> {
        return fetchWeather {
            Log.d(TAG, "Fetching weather for zip code: $zipCode")
            weatherApiService.getCurrentWeatherByZip(zipCode)
        }
    }

    //getting weather by coordinates
    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): Result<WeatherResponse> {
        return fetchWeather {
            Log.d(TAG, "Fetching weather for coordinates: lat=$lat, lon=$lon")
            weatherApiService.getCurrentWeatherByCoords(lat, lon)
        }
    }

    //generic helper to fetch weather and handle exceptions
    private suspend fun fetchWeather(apiCall: suspend () -> WeatherResponse): Result<WeatherResponse> {
        return try {
            //executing API call on the IO dispatcher
            //(so main thread doesn't get blocked)
            val response = withContext(Dispatchers.IO) {
                apiCall()
            }
            Result.success(response)
        } catch (e: HttpException) {
            //http error handling
            Log.e(TAG, "HTTP error: ${e.code()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            //network error handling
            Log.e(TAG, "Network error", e)
            Result.failure(e)
        } catch (e: Exception) {
            //any other errors
            Log.e(TAG, "Unexpected error", e)
            Result.failure(e)
        }
    }
}