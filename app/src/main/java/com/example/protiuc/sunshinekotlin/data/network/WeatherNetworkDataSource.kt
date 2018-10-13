package com.example.protiuc.sunshinekotlin.data.network

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.example.protiuc.sunshinekotlin.AppExecutors
import com.example.protiuc.sunshinekotlin.data.database.WeatherEntry
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides an API for doing all operations with the server data
 */
@Singleton
class WeatherNetworkDataSource @Inject constructor(private val mExecutors: AppExecutors) {
    private val downloadedWeatherForecasts: MutableLiveData<Array<WeatherEntry>> = MutableLiveData()

    val currentWeatherForecasts: LiveData<Array<WeatherEntry>>
        get() = downloadedWeatherForecasts

    /**
     * Starts an intent service to fetch the weather.
     */
    fun startFetchWeatherService() {
        fetchWeather()
        Log.d(LOG_TAG, "Service created")
    }

    /**
     * Gets the newest weather
     */
    internal fun fetchWeather() {
        Log.d(LOG_TAG, "Fetch weather started")
        mExecutors.networkIO().execute {
            try {

                // The getUrl method will return the URL that we need to get the forecast JSON for the
                // weather. It will decide whether to create a URL based off of the latitude and
                // longitude or off of a simple location as a String.

                val weatherRequestUrl = NetworkUtils.url

                // Use the URL to retrieve the JSON
                val jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl!!)

                // Parse the JSON into a list of weather forecasts
                val response = OpenWeatherJsonParser().parse(jsonWeatherResponse!!)
                Log.d(LOG_TAG, "JSON Parsing finished")


                // As long as there are weather forecasts, update the LiveData storing the most recent
                // weather forecasts. This will trigger observers of that LiveData, such as the
                // SunshineRepository.
                if (response != null && response.weatherForecast.isNotEmpty()) {
                    Log.d(LOG_TAG, "JSON not null and has " + response.weatherForecast.size
                            + " values")
                    Log.d(LOG_TAG, String.format("First value is %1.0f and %1.0f",
                            response.weatherForecast[0].min,
                            response.weatherForecast[0].max))

                    // TODO Finish this method when instructed.
                    // Will eventually do something with the downloaded data
                    downloadedWeatherForecasts.postValue(response.weatherForecast)
                }
            } catch (e: Exception) {
                // Server probably invalid
                e.printStackTrace()
            }
        }
    }

    companion object {
        // The number of days we want our API to return, set to 14 days or two weeks
        val NUM_DAYS = 14
        private val LOG_TAG = WeatherNetworkDataSource::class.java.simpleName

        // Interval at which to sync with the weather. Use TimeUnit for convenience, rather than
        // writing out a bunch of multiplication ourselves and risk making a silly mistake.
        private val SYNC_INTERVAL_HOURS = 3
        private val SYNC_INTERVAL_SECONDS = TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS.toLong()).toInt()
        private val SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3
        private val SUNSHINE_SYNC_TAG = "sunshine-sync"
    }
}