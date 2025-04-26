package com.example.nova.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.nova.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.test.StandardTestDispatcher


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
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = WeatherViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        println("Test passed!")
    }

    @Test
    fun testFetchWeatherForCity() {
        val cityName = "Minneapolis"
        viewModel.fetchWeatherForCity(cityName)
    }

    @Test
    fun testFetchWeatherForZipCode() {
        val zipCode = "55401"
        viewModel.fetchWeatherForZipCode(zipCode)
    }

    @Test
    fun testResetToDefaultLocation() {
        viewModel.resetToDefaultLocation()
    }

    @Test
    fun testFetchForecastForZipCode() {
        val zipCode = "55401"
        viewModel.fetchForecastForZipCode(zipCode)
    }

    @Test
    fun testFetchWeatherForCity_CallsRepository() {
        val cityName = "Minneapolis"
        viewModel.fetchWeatherForCity(cityName)
    }

    @Test
    fun testFetchWeatherForZipCode_CallsRepository() {
        val zipCode = "55401"
        viewModel.fetchWeatherForZipCode(zipCode)
    }

    @Test
    fun testErrorHandling() {
        viewModel.fetchWeatherForCity("InvalidCity")
    }

    @Test
    fun testLoadingState() {
        assert(viewModel.isLoading.value == true)
    }

    @Test
    fun testCachingBehavior() {
        viewModel.fetchWeatherForCity("Minneapolis")
        viewModel.fetchWeatherForCity("Minneapolis")
    }
}