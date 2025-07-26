# Nova Weather

An Android weather application built with Kotlin and Jetpack Compose, utilizing a location service and the OpenWeatherMap API to deliver real-time local weather updates and forecasts.


[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Table of Contents

*   [About The Project](#about-the-project)
*   [Screenshots](#screenshots)
*   [Features](#features)
*   [Tech Stack](#tech-stack)
*   [Getting Started](#getting-started)
    *   [Prerequisites](#prerequisites)
    *   [Installation & Setup](#installation--setup)
*   [Usage](#usage)
*   [API Key Setup](#api-key-setup)




## About The Project

Nova is designed to provide a clean, modern, and user-friendly experience for checking current weather conditions and forescasts.

It leverages your device's location to provide accurate local weather data and also allows users to search for weather in other locations.

The app is build following modern Android development practices.

## Screenshots

| Current Weather | Forecast Screen |
|---|---|
| ![Current Weather Screen](assets/Nova-CurrentWeather-sc.png) | ![Forecast Screen](assets/Nova-Forecast-sc.png) |

## Features
*   **Current Weather:** Displays current temperature, feels-like temperature, conditions (e.g., sunny, cloudy), humidity, wind speed, etc.
*   **Location-Based Weather:** Automatically fetches weather for the user's current location (requires location permission).
*   **Search Functionality:** Allows users to search for weather in any city worldwide.
*   **Weather Condition Icons:** Visually represents current weather conditions.
*   **Material Design UI:** Modern and intuitive user interface adhering to Material Design guidelines.

## Tech Stack

*   **Programming Language:** [Kotlin](https://kotlinlang.org/)
*   **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Asynchronous Programming:** Kotlin Coroutines
*   **Networking:**
    *   [Retrofit](https://square.github.io/retrofit/) - For making API calls
    *   [OkHttp](https://square.github.io/okhttp/) - HTTP client
    *   [Gson](https://github.com/google/gson) - For JSON parsing
*   **Android Jetpack Components:**
    *   ViewModel
    *   LiveData
    *   Navigation Compose
    *   Service
*   **API:** [OpenWeatherMap API](https://openweathermap.org/api)
*   **Build Tool:** Gradle

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

*   Android Studio
*   Android SDK
*   An OpenWeatherMap API Key (see [API Key Setup](#api-key-setup))

### Installation & Setup

1.  **Clone the repository:**
2.  **Open in Android Studio:**
3.  **API Key:**
    You will need to add your own OpenWeatherMap API key. See the [API Key Setup](#api-key-setup) section below.
4.  **Build the project:**
    Android Studio should automatically sync Gradle and download dependencies.
5.  **Run the app:**
    Select an emulator or connect a physical device and run the app.

## Usage
*   Upon first launch, the app may request location permission to display local weather.
*   Use the search bar to find weather for a specific city.
*   Tap on a day in the forecast (if applicable) for more details.
*   Click the "My Location" icon to refresh weather for your current position.

## API Key Setup

This project uses the OpenWeatherMap API to fetch weather data. You'll need to obtain your own free API key.

1.  Go to [OpenWeatherMap](https://openweathermap.org/appid) and sign up.
2.  Navigate to the "API keys" tab on your account page to find your default API key.
3.  Once you have your API key, you need to add it to the project. This project expects the API key to be available via `BuildConfig`.
4.  Open your `app/build.gradle.kts` file.
5.  Locate the `defaultConfig` block and ensure the `buildConfigField` for the API key is present.
6.  Replace `"YOUR_API_KEY_HERE"` with your actual OpenWeatherMap API key.
7.  Enjoy!
