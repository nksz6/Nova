//ICS 342 NICK KELLEY

package com.example.nova.data.network

//imports
import com.example.nova.BuildConfig
import com.example.nova.data.model.WeatherResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.example.nova.data.model.ForecastResponse

//retrofit interface for API calls
interface WeatherApiService {

    //getting weather by city name
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") location: String,
        @Query("units") units: String = DEFAULT_UNITS,
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): WeatherResponse

    //getting weather by zip
    @GET("weather")
    suspend fun getCurrentWeatherByZip(
        @Query("zip") zipCode: String,
        @Query("units") units: String = DEFAULT_UNITS,
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): WeatherResponse

    //getting weather by literal coordinates (coords)
    @GET("weather")
    suspend fun getCurrentWeatherByCoords(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = DEFAULT_UNITS,
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): WeatherResponse

    //getting 16-day forecast by zip
    @GET("forecast/daily")
    suspend fun getForecastByZip(
        //QUERY PARAMETERS:
        @Query("zip") zipCode: String,  //'zipcode' = the 5 digit zip code that will be inputted as a string
        @Query("cnt") count: Int = 16, //'count' = the number of days to forecast, defaulting to 16 days
        @Query("units") units: String = DEFAULT_UNITS,  //'units' = using the existing 'DEFAULT_UNITS' variable (which is imperial)
        @Query("appid") apiKey: String = BuildConfig.API_KEY    //'apiKey' = my API key for OpenWeatherMap
    ): ForecastResponse

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        private const val DEFAULT_UNITS = "imperial" //i want fahrenheit, not celsius
        private const val TIMEOUT_SECONDS = 30L

        //creating an instance of WeatherApiService
        fun create(): WeatherApiService {
            //logger is a logging interceptor for debugging, i leaving this in w/ assignment3 submission
            val logger = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BASIC
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

            //so this is configuring a OkHttpClient
            //has timeouts and logging
            //not entirely clear on this, obviously I had some help.
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()

            //create and configure retrofit
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}