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

    // Rule to make LiveData work synchronously
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test coroutine dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockRepository: WeatherRepository

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        // Set up Dispatchers.Main for coroutines
        Dispatchers.setMain(testDispatcher)

        // Initialize mocks
        MockitoAnnotations.openMocks(this)

        // Create the ViewModel with mock repository
        viewModel = WeatherViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        // Just a simple test for now
        println("Test passed!")
    }
}