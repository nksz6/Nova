package com.example.nova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nova.ui.theme.NovaTheme
import androidx.compose.ui.graphics.Color



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NovaTheme {
                Scaffold(
                    topBar = {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF795548),
                            tonalElevation = 10.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 25.dp)
                                    .padding(start = 10.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(top = 12.5.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    WeatherScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        //Weather Icon
        Image(
            painter = painterResource(id = R.drawable.ic_sunny),
            contentDescription = stringResource(id = R.string.weather_condition),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(vertical = 45.dp)
                .padding(horizontal = 25.dp)
                .size(120.dp)
        )

        //Temperature
        Text(
            text = stringResource(id = R.string.current_temperature),
            fontSize = 75.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 50.dp)
                .padding(horizontal = 25.dp)

        )

        //Location
        Text(
            text = stringResource(id = R.string.location),
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 16.dp)
        )

        //Feels like
        Text(
            text = stringResource(id = R.string.feels_like),
            fontSize = 25.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 140.dp)
                .padding(horizontal = 10.dp) //seemed roughly center under temp?
        )

        //Low Temperature
        Text(
            text = stringResource(id = R.string.low_temp),
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 225.dp) //starting at 225.dp
                .padding(horizontal = 18.dp)
        )

        //High Temperature
        Text(
            text = stringResource(id = R.string.high_temp),
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 255.dp) //spaced by 30
                .padding(horizontal = 18.dp)
        )

        //Humidity
        Text(
            text = stringResource(id = R.string.humidity),
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 285.dp) //spaced by 30
                .padding(horizontal = 18.dp)
        )

        //Pressure
        Text(
            text = stringResource(id = R.string.pressure),
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 315.dp) //spaced by 30
                .padding(horizontal = 18.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    NovaTheme {
        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 3.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        ) { innerPadding ->
            WeatherScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}