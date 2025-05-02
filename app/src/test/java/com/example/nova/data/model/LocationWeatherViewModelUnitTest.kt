package com.example.nova.data.model

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import java.net.SocketTimeoutException


@ExperimentalCoroutinesApi
class LocationWeatherViewModelUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockRepository: WeatherRepository
    private lateinit var viewModel: LocationWeatherViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LocationWeatherViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSetLocationEnabled_True() {
        viewModel.setLocationEnabled(true)
        assert(viewModel.isLocationEnabled.value == true)
    }

    @Test
    fun testSetLocationEnabled_False() {
        viewModel.setLocationEnabled(true)
        assert(viewModel.isLocationEnabled.value == true)

        viewModel.setLocationEnabled(false)
        assert(viewModel.isLocationEnabled.value == false)
        assert(viewModel.locationWeatherData.value == null)
    }

    @Test
    fun testFetchWeatherForLocation_Success() = runTest {
        //mock location
        val mockLocation = mock(Location::class.java)
        `when`(mockLocation.latitude).thenReturn(44.9778)
        `when`(mockLocation.longitude).thenReturn(-93.2650)
        //mock weather response
        val mockWeatherResponse = createMockWeatherResponse()
        //setup return success
        `when`(mockRepository.getWeatherByCoordinates(44.9778, -93.2650))
            .thenReturn(Result.success(mockWeatherResponse))
        //call
        viewModel.fetchWeatherForLocation(mockLocation)
        //advance test dispatcher until coroutines complete
        advanceUntilIdle()
        //verify
        verify(mockRepository).getWeatherByCoordinates(44.9778, -93.2650)
    }

    @Test
    fun testFetchWeatherForLocation_Error_Http() = runTest {
        //mock location
        val mockLocation = mock(Location::class.java)
        `when`(mockLocation.latitude).thenReturn(44.9778)
        `when`(mockLocation.longitude).thenReturn(-93.2650)

        //mock HttpException
        val httpException = mock(HttpException::class.java)
        `when`(httpException.code()).thenReturn(404)

        //setup return failure
        `when`(mockRepository.getWeatherByCoordinates(44.9778, -93.2650))
            .thenReturn(Result.failure(httpException))

        //call, advance, verify
        viewModel.fetchWeatherForLocation(mockLocation)
        advanceUntilIdle()
        verify(mockRepository).getWeatherByCoordinates(44.9778, -93.2650)

        //assert
        assert(viewModel.error.value != null)
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testFetchWeatherForLocation_Error_Network() = runTest {
        //mock location
        val mockLocation = mock(Location::class.java)
        `when`(mockLocation.latitude).thenReturn(44.9778)
        `when`(mockLocation.longitude).thenReturn(-93.2650)

        //network error
        val networkException = UnknownHostException("No Internet Connection")

        //setup return failure
        `when`(mockRepository.getWeatherByCoordinates(44.9778, -93.2650))
            .thenReturn(Result.failure(networkException))

        //call, advance, verify
        viewModel.fetchWeatherForLocation(mockLocation)
        advanceUntilIdle()
        verify(mockRepository).getWeatherByCoordinates(44.9778, -93.2650)

        //assert
        assert(viewModel.error.value != null)
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testFetchWeatherForLocation_Error_Timeout() = runTest {
        val mockLocation = mock(Location::class.java)
        `when`(mockLocation.latitude).thenReturn(44.9778)
        `when`(mockLocation.longitude).thenReturn(-93.2650)

        //set timeout error
        val timeoutException = SocketTimeoutException("Connection timed out")

        //set return fail
        `when`(mockRepository.getWeatherByCoordinates(44.9778, -93.2650))
            .thenReturn(Result.failure(timeoutException))

        //call, advance, verify
        viewModel.fetchWeatherForLocation(mockLocation)
        advanceUntilIdle()
        verify(mockRepository).getWeatherByCoordinates(44.9778, -93.2650)

        //assert
        assert(viewModel.error.value != null)
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testFetchWeatherForLocation_Error_Other() = runTest {
        val mockLocation = mock(Location::class.java)
        `when`(mockLocation.latitude).thenReturn(44.9778)
        `when`(mockLocation.longitude).thenReturn(-93.2650)

        //set general error
        val generalException = Exception("General error")

        //set return failure
        `when`(mockRepository.getWeatherByCoordinates(44.9778, -93.2650))
            .thenReturn(Result.failure(generalException))

        //call, advance, verify
        viewModel.fetchWeatherForLocation(mockLocation)
        advanceUntilIdle()
        verify(mockRepository).getWeatherByCoordinates(44.9778, -93.2650)

        //assert
        assert(viewModel.error.value != null)
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testCaching_SameLocation() = runTest {
        val mockLocation = mock(Location::class.java)
        `when`(mockLocation.latitude).thenReturn(44.9778)
        `when`(mockLocation.longitude).thenReturn(-93.2650)

        //set response success
        val mockWeatherResponse = createMockWeatherResponse()
        `when`(mockRepository.getWeatherByCoordinates(44.9778, -93.2650))
            .thenReturn(Result.success(mockWeatherResponse))

        //caching - return same
        `when`(mockLocation.distanceTo(mockLocation)).thenReturn(0f)

        //call, advance
        viewModel.fetchWeatherForLocation(mockLocation)
        advanceUntilIdle()

        //reset mock
        clearInvocations(mockRepository)

        //second call, advance
        viewModel.fetchWeatherForLocation(mockLocation)
        advanceUntilIdle()

        //verify
        verify(mockRepository, never()).getWeatherByCoordinates(anyDouble(), anyDouble())
    }

    @Test
    fun testCaching_DifferentLocation() = runTest {
        //mock loc 1
        val mockLocation1 = mock(Location::class.java)
        `when`(mockLocation1.latitude).thenReturn(44.9778)
        `when`(mockLocation1.longitude).thenReturn(-93.2650)

        //mock loc 2
        val mockLocation2 = mock(Location::class.java)
        `when`(mockLocation2.latitude).thenReturn(40.7128)
        `when`(mockLocation2.longitude).thenReturn(-74.0060)

        //set distance > cache threshold
        `when`(mockLocation1.distanceTo(mockLocation2)).thenReturn(2000f)
        `when`(mockLocation2.distanceTo(mockLocation1)).thenReturn(2000f)

        //set mock WeatherResponse(s)
        val mockWeatherResponse1 = createMockWeatherResponse()
        val mockWeatherResponse2 = createMockWeatherResponse()

        //set repo return success for both
        `when`(mockRepository.getWeatherByCoordinates(44.9778, -93.2650))
            .thenReturn(Result.success(mockWeatherResponse1))
        `when`(mockRepository.getWeatherByCoordinates(40.7128, -74.0060))
            .thenReturn(Result.success(mockWeatherResponse2))

        //1st call, advance, reset
        viewModel.fetchWeatherForLocation(mockLocation1)
        advanceUntilIdle()
        clearInvocations(mockRepository)

        //2nd call, advance
        viewModel.fetchWeatherForLocation(mockLocation2)
        advanceUntilIdle()

        //verify 2nd call with those coords
        verify(mockRepository).getWeatherByCoordinates(40.7128, -74.0060)
    }

    //helper for mocking
    private fun createMockWeatherResponse(): WeatherResponse {
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
        val sys = Sys(country = "US", sunrise = 1677924458, sunset = 1677966890)

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
            name = "Minneapolis",
            cod = 200
        )
    }
}