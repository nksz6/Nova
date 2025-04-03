//ICS342 - NICK KELLEY

//package
package com.example.nova.utils

//imports
import androidx.compose.runtime.Composable
//import androidx.compose.ui.res.painterResource
import com.example.nova.R

//helper function letting the screens show icons for the certain weather conditions
@Composable
fun getWeatherIcon(iconCode: String): Int {
    return when (iconCode.take(2)) { //Only take the first two, not caring about day/night
        "01" -> R.drawable.ic_sunny
        "02" -> R.drawable.ic_few_clouds
        "03" -> R.drawable.ic_scattered_clouds
        "04" -> R.drawable.ic_broken_clouds
        "09" -> R.drawable.ic_shower_rain
        "10" -> R.drawable.ic_rain
        "11" -> R.drawable.ic_thunderstorm
        "13" -> R.drawable.ic_snowing
        "50" -> R.drawable.ic_mist

        else -> R.drawable.ic_sunny //default image if I don't have one for the current conditions
    }
}

