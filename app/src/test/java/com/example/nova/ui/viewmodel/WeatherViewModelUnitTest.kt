@file:Suppress("SameParameterValue", "SameParameterValue")

package com.example.nova.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.nova.data.model.*
import com.example.nova.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import java.net.UnknownHostException


@Suppress("SameParameterValue", "SameParameterValue")
@ExperimentalCoroutinesApi
class WeatherViewModelUnitTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockRepository: WeatherRepository
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        //creating viewmodel without repo setup
        viewModel = WeatherViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest {
        //setup default location response
        val defaultWeatherResponse = createMockWeatherResponse("Minnetrista", "US")
        `when`(mockRepository.getWeatherByCity("Minnetrista,MN,US"))
            .thenReturn(Result.success(defaultWeatherResponse))

        //adv
        advanceUntilIdle()

        //verify
        verify(mockRepository).getWeatherByCity("Minnetrista,MN,US")
    }

    @Test
    fun testFetchWeatherForCity_Success() = runTest {
        //mock w/ default location
        val defaultWeatherResponse = createMockWeatherResponse("Minnetrista", "US")
        `when`(mockRepository.getWeatherByCity("Minnetrista,MN,US"))
            .thenReturn(Result.success(defaultWeatherResponse))

        //adv, clear/reset
        advanceUntilIdle()
        clearInvocations(mockRepository)

        //setup
        val cityName = "Minneapolis"
        val mockResponse = createMockWeatherResponse(cityName, "US")
        `when`(mockRepository.getWeatherByCity(cityName))
            .thenReturn(Result.success(mockResponse))

        //call, advance, verify
        viewModel.fetchWeatherForCity(cityName)
        advanceUntilIdle()
        verify(mockRepository).getWeatherByCity(cityName)

        //assert
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testFetchWeatherForZipCode_Success() = runTest {
        //mock w/ default
        val defaultWeatherResponse = createMockWeatherResponse("Minnetrista", "US")
        `when`(mockRepository.getWeatherByCity("Minnetrista,MN,US"))
            .thenReturn(Result.success(defaultWeatherResponse))

        //adv, clear
        advanceUntilIdle()
        clearInvocations(mockRepository)

        //setup
        val zipCode = "55401"
        val mockResponse = createMockWeatherResponse("Minneapolis", "US")
        `when`(mockRepository.getWeatherByZip(zipCode))
            .thenReturn(Result.success(mockResponse))

        //call, advance, verify
        viewModel.fetchWeatherForZipCode(zipCode)
        advanceUntilIdle()
        verify(mockRepository).getWeatherByZip(zipCode)

        //assert
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testResetToDefaultLocation() = runTest {
        val defaultWeatherResponse = createMockWeatherResponse("Minnetrista", "US")
        `when`(mockRepository.getWeatherByCity("Minnetrista,MN,US"))
            .thenReturn(Result.success(defaultWeatherResponse))

        advanceUntilIdle()
        clearInvocations(mockRepository)

        viewModel.resetToDefaultLocation()

        advanceUntilIdle()
        verify(mockRepository).getWeatherByCity("Minnetrista,MN,US")
    }

    @Test
    fun testFetchForecastForZipCode_Success() = runTest {
        //mock, adv
        val defaultWeatherResponse = createMockWeatherResponse("Minnetrista", "US")
        `when`(mockRepository.getWeatherByCity("Minnetrista,MN,US"))
            .thenReturn(Result.success(defaultWeatherResponse))
        advanceUntilIdle()

        //first load zip weather data
        val zipCode = "55401"
        val mockWeatherResponse = createMockWeatherResponse("Minneapolis", "US")
        `when`(mockRepository.getWeatherByZip(zipCode))
            .thenReturn(Result.success(mockWeatherResponse))

        //call, adv and reset
        viewModel.fetchWeatherForZipCode(zipCode)
        advanceUntilIdle()
        clearInvocations(mockRepository)

        //setup forecast response
        val mockForecastResponse = createMockForecastResponse("Minneapolis")
        `when`(mockRepository.getForecastByZip(zipCode))
            .thenReturn(Result.success(mockForecastResponse))

        //fetch forecast, adv and verify
        viewModel.fetchForecastForZipCode(zipCode)
        advanceUntilIdle()
        verify(mockRepository).getForecastByZip(zipCode)
    }

    @Test
    fun testError_CityNotFound() = runTest {
        //set mock, adv and clear
        val defaultWeatherResponse = createMockWeatherResponse("Minnetrista", "US")
        `when`(mockRepository.getWeatherByCity("Minnetrista,MN,US"))
            .thenReturn(Result.success(defaultWeatherResponse))
        advanceUntilIdle()
        clearInvocations(mockRepository)

        //setup fake city
        val cityName = "FakeTownUSA"
        val httpException = mock(HttpException::class.java)
        `when`(httpException.code()).thenReturn(404)
        `when`(mockRepository.getWeatherByCity(cityName))
            .thenReturn(Result.failure(httpException))

        //call, adv, verify
        viewModel.fetchWeatherForCity(cityName)
        advanceUntilIdle()
        verify(mockRepository).getWeatherByCity(cityName)

        //assert
        assert(viewModel.error.value != null)
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testError_NetworkError() = runTest {
        //mock, adv, clear
        val defaultWeatherResponse = createMockWeatherResponse("Minnetrista", "US")
        `when`(mockRepository.getWeatherByCity("Minnetrista,MN,US"))
            .thenReturn(Result.success(defaultWeatherResponse))
        advanceUntilIdle()
        clearInvocations(mockRepository)

        //setup w/ minneapolis but no internet
        val cityName = "Minneapolis"
        val networkException = UnknownHostException("No Internet Connection")
        `when`(mockRepository.getWeatherByCity(cityName))
            .thenReturn(Result.failure(networkException))

        //call, adv, verify
        viewModel.fetchWeatherForCity(cityName)
        advanceUntilIdle()
        verify(mockRepository).getWeatherByCity(cityName)

        //assert
        assert(viewModel.error.value != null)
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testCaching_SameLocation() = runTest {
        //mock, adv, clear
        val defaultWeatherResponse = createMockWeatherResponse("Minnetrista", "US")
        `when`(mockRepository.getWeatherByCity("Minnetrista,MN,US"))
            .thenReturn(Result.success(defaultWeatherResponse))
        advanceUntilIdle()
        clearInvocations(mockRepository)

        //setup w. minneapolis
        val cityName = "Minneapolis"
        val mockResponse = createMockWeatherResponse(cityName, "US")
        `when`(mockRepository.getWeatherByCity(cityName))
            .thenReturn(Result.success(mockResponse))

        //call 1, adv and reset
        viewModel.fetchWeatherForCity(cityName)
        advanceUntilIdle()
        clearInvocations(mockRepository)

        //2nd call using cache
        viewModel.fetchWeatherForCity(cityName)
        advanceUntilIdle()

        //verify not called twice
        verify(mockRepository, never()).getWeatherByCity(cityName)
    }

    //helper methods
    private fun createMockWeatherResponse(cityName: String, country: String): WeatherResponse {
        val coordinates = Coordinates(lon = -93.2650, lat = 44.9778)
        val weather = Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")
        val main = Main(
            temp = 75.0,
            feelsLike = 72.0,
            tempMin = 70.0,
            tempMax = 78.0,
            pressure = 1015,
            humidity = 45
        )
        val wind = Wind(speed = 5.0, deg = 270)
        val clouds = Clouds(all = 0)
        val sys = Sys(country = country, sunrise = 1677924458, sunset = 1677966890)

        return WeatherResponse(
            coord = coordinates,
            weather = listOf(weather),
            base = "stations",
            main = main,
            visibility = 10000,
            wind = wind,
            clouds = clouds,
            dt = 1677945600,
            sys = sys,
            timezone = -21600,
            id = 5037649,
            name = cityName,
            cod = 200
        )
    }

    private fun createMockForecastResponse(cityName: String): ForecastResponse {
        val coordinates = Coordinates(lon = -93.2650, lat = 44.9778)
        val city = City(
            id = 5037649,
            name = cityName,
            coord = coordinates,
            country = "US",
            population = 429606,
            timezone = -21600
        )

        val weather = Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")

        val forecasts = mutableListOf<DailyForecast>()
        for (i in 0 until 16) {
            val temp = Temperature(
                day = 75.0,
                min = 65.0,
                max = 80.0,
                night = 60.0,
                evening = 70.0,
                morning = 68.0
            )

            val feelsLike = FeelsLike(
                day = 72.0,
                night = 58.0,
                evening = 68.0,
                morning = 66.0
            )

            val forecast = DailyForecast(
                dt = 1677945600L + (i * 86400L), // Add one day per forecast
                sunrise = 1677924458L + (i * 86400L),
                sunset = 1677966890L + (i * 86400L),
                temp = temp,
                feelsLike = feelsLike,
                pressure = 1015,
                humidity = 45,
                weather = listOf(weather),
                windSpeed = 5.0,
                windDeg = 270,
                cloudiness = 0,
                probabilityOfPrecipitation = 0.0
            )

            forecasts.add(forecast)
        }

        return ForecastResponse(
            city = city,
            cod = "200",
            message = 0.0,
            cnt = 16,
            list = forecasts
        )
    }
}