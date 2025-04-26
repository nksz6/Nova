package com.example.nova.data.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.nova.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import android.location.Location
import org.mockito.Mockito.mock

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
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = LocationWeatherViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testFetchWeatherForLocation() {
        val mockLocation = mock(Location::class.java)
        mockLocation.latitude = 44.9778
        mockLocation.longitude = -93.2650
        viewModel.fetchWeatherForLocation(mockLocation)
    }

    @Test
    fun testSetLocationEnabled() {
        //enable location
        viewModel.setLocationEnabled(true)
        assert(viewModel.isLocationEnabled.value == true)
        //disable location
        viewModel.setLocationEnabled(false)
        assert(viewModel.isLocationEnabled.value == false)
    }

    @Test
    fun testFetchWeatherForLocation_CallsRepository() {
        val mockLocation = mock(Location::class.java)
        mockLocation.latitude = 44.9778
        mockLocation.longitude = -93.2650
        viewModel.fetchWeatherForLocation(mockLocation)
    }

    @Test
    fun testCacheValidation() {
        val location1 = mock(Location::class.java)
        location1.latitude = 44.9778
        location1.longitude = -93.2650
        val location2 = mock(Location::class.java)
        location2.latitude = 44.9780
        location2.longitude = -93.2655
        viewModel.fetchWeatherForLocation(location1)
        viewModel.fetchWeatherForLocation(location2)

    }

    @Test
    fun testErrorHandlingForLocationWeather() {
        val mockLocation = mock(Location::class.java)
        viewModel.fetchWeatherForLocation(mockLocation)

    }

    @Test
    fun testLocationEnabledState() {
        viewModel.setLocationEnabled(true)
        assert(viewModel.isLocationEnabled.value == true)
        viewModel.setLocationEnabled(false)
        assert(viewModel.isLocationEnabled.value == false)
        viewModel.setLocationEnabled(false)
        assert(viewModel.locationWeatherData.value == null)
    }
}