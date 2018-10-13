package com.example.protiuc.sunshinekotlin.data

import android.arch.lifecycle.LiveData
import android.util.Log
import com.example.protiuc.sunshinekotlin.AppExecutors
import com.example.protiuc.sunshinekotlin.data.database.WeatherDao
import com.example.protiuc.sunshinekotlin.data.database.WeatherEntry
import com.example.protiuc.sunshinekotlin.data.network.WeatherNetworkDataSource
import com.example.protiuc.sunshinekotlin.utils.SunshineDateUtils
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SunshineRepository @Inject constructor(
        private val weatherDao: WeatherDao,
        private val weatherNetworkDataSource: WeatherNetworkDataSource,
        private val executors: AppExecutors) {

    companion object {
        private val LOG_TAG = SunshineRepository::class.java.simpleName
    }

    private var mInitialized = false

    init {
        val networkData = weatherNetworkDataSource.currentWeatherForecasts
        networkData.observeForever { newForecastsFromNetwork ->
            executors.diskIO().execute {
                // Insert our new weather data into Sunshine's database
                weatherDao.bulkInsert(newForecastsFromNetwork!!)
                Log.d(LOG_TAG, "New values inserted")
            }
        }
    }

    fun getWeatherbyDate(date: Date): LiveData<WeatherEntry> {
        initializeData()
        return weatherDao.getWeatherByDate(date)
    }

    fun getCurrentWeatherForecasts(): LiveData<List<WeatherEntry>> {
        initializeData()
        val today = SunshineDateUtils.normalizedUtcDateForToday
        return weatherDao.getCurrentWeatherForecasts(today)
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    @Synchronized
    private fun initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return
        mInitialized = true

        executors.diskIO().execute {
            if (isFetchNeeded()) {
                startFetchWeatherService()
            }
        }
    }

    /**
     * Database related operations
     **/

    /**
     * Deletes old weather data because we don't need to keep multiple days' data
     */
    private fun deleteOldData() {
        val today = SunshineDateUtils.normalizedUtcDateForToday
        weatherDao.deleteOldData(today)
    }

    /**
     * Checks if there are enough days of future weather for the app to display all the needed data.
     *
     * @return Whether a fetch is needed
     */
    private fun isFetchNeeded(): Boolean {
        val today = SunshineDateUtils.normalizedUtcDateForToday
        val count = weatherDao.countAllFutureWeather(today)
        return count < WeatherNetworkDataSource.NUM_DAYS
    }

    /**
     * Network related operation
     */

    private fun startFetchWeatherService() {
        weatherNetworkDataSource.startFetchWeatherService()
    }
}