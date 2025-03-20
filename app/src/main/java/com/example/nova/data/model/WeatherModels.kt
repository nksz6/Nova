//ICS342 NICK KELLEY

package com.example.nova.data.model
import com.google.gson.annotations.SerializedName

//this data class should represent everything the API might return
//seems like a lot but what the hell...
data class WeatherResponse(
    val coord: Coordinates,          //'coord' is city coordinates
    val weather: List<Weather>,      //weather conditions, and it may contain multiple
    val base: String,                //internal parameter
    val main: Main,                  //main weather data, like temp, pressure, etc..
    val visibility: Int,             //visibility (fyi denoted in meters)
    val wind: Wind,                  //wind information
    val clouds: Clouds,              //cloud/cloudiness information
    val dt: Long,                    //so 'dt' is a unix timestamp for when the data was calculated
    val sys: Sys,                    //Sys is system information, like country, sunrise, sunset etc..
    val timezone: Int,               //timezone shift from UTC in seconds
    val id: Int,                     //city ID
    val name: String,                //city name
    val cod: Int                     //internal parameter
)

//location coordinates!
data class Coordinates(
    val lon: Double,     //longitude
    val lat: Double      //latitude
)

//weather condition info
data class Weather(
    val id: Int,              //weather condition ID
    val main: String,         //group of weather parameters (Rain, Snow, etc.)
    val description: String,  //weather condition within the group
    val icon: String          //weather icon ID
)

//main weather measurements
data class Main(
    val temp: Double,                    //current temperature
    @SerializedName("feels_like")
    val feelsLike: Double,               //temperature accounting for human perception
    @SerializedName("temp_min")
    val tempMin: Double,                 //minimum temperature
    @SerializedName("temp_max")
    val tempMax: Double,                 //maximum temperature
    val pressure: Int,                   //atmospheric pressure (hPa)
    val humidity: Int,                   //humidity percentage
    @SerializedName("sea_level")
    val seaLevel: Int? = null,           //atmospheric pressure at sea level
    @SerializedName("grnd_level")
    val groundLevel: Int? = null         //atmospheric pressure at ground level
)

//wind information
data class Wind(
    val speed: Double,     //wind speed (default: meters/sec)
    val deg: Int,          //wind direction in degrees
    val gust: Double? = null  //wind gust
)

//cloudiness information
data class Clouds(
    val all: Int           //cloudiness percentage
)

//system parameters
data class Sys(
    val type: Int? = null,    //internal parameter 1
    val id: Int? = null,      //internal parameter 2
    val country: String,      //country code
    val sunrise: Long,        //sunrise time (Unix UTC)
    val sunset: Long          //sunset time (Unix UTC)
)

