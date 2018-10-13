package com.example.protiuc.sunshinekotlin.data.network

import com.example.protiuc.sunshinekotlin.data.database.WeatherEntry

/**
 * Weather response from the backend. Contains the weather forecasts.
 */
internal class WeatherResponse(val weatherForecast: Array<WeatherEntry>)