//ICS342 NICK KELLEY
package com.example.nova.data.model

//import
import com.google.gson.annotations.SerializedName

// main response class for the 16-day forecast API
data class ForecastResponse(
    val city: City,                //city information (name, coordinates, etc.)
    val cod: String,               //response code
    val message: Double,           //response message
    val cnt: Int,                  //number of days to be returned (16)
    val list: List<DailyForecast>  //list of daily forecast data
)

//City info
data class City(
    val id: Int,                   //city ID
    val name: String,              //city name
    val coord: Coordinates,        //city coordinates (coords)
    val country: String,           //city code
    val population: Int,           //city population
    val timezone: Int              //timezone offset (from utc to secs)
)

//note - am using serialized names on some for where the JSON fields are different from the variable names

//each day's forecast info
data class DailyForecast(
    val dt: Long,                  //timestamp for the forecasted data (unix, utc)
    val sunrise: Long,             //sunrise time (unix, utc)
    val sunset: Long,              //sunset time (unix, utc)
    val temp: Temperature,         //temperature info
    val feelsLike: FeelsLike,      //"feels-like" temp
    @SerializedName("pressure")
    val pressure: Int,             //atmospheric pressure
    @SerializedName("humidity")
    val humidity: Int,             //humidity percentage
    val weather: List<Weather>,    //weather conditions (existing Weather class)
    @SerializedName("speed")
    val windSpeed: Double,         //wind speed
    @SerializedName("deg")
    val windDeg: Int,              //wind direction, degrees
    @SerializedName("clouds")
    val cloudiness: Int,           //cloudiness percentage
    @SerializedName("rain")
    val rain: Double? = null,      //rain volume (optional)
    @SerializedName("snow")
    val snow: Double? = null,      //snow volume (optional)
    @SerializedName("pop")
    val probabilityOfPrecipitation: Double //percipitation probability
)

//day's temp info
data class Temperature(
    @SerializedName("day")
    val day: Double,               //day temperature
    @SerializedName("min")
    val min: Double,               //minimum temperature
    @SerializedName("max")
    val max: Double,               //maximum temperature
    @SerializedName("night")
    val night: Double,             //night temperature
    @SerializedName("eve")
    val evening: Double,           //evening temperature
    @SerializedName("morn")
    val morning: Double            //morning temperature
)

//"feels-like" temp info
data class FeelsLike(
    @SerializedName("day")
    val day: Double,               //feels-like for day
    @SerializedName("night")
    val night: Double,             //feels-like for night
    @SerializedName("eve")
    val evening: Double,           //feels-like for evening
    @SerializedName("morn")
    val morning: Double            //feels-like" for morning
)

