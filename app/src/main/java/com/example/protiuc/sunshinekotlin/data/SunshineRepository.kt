package com.example.protiuc.sunshinekotlin.data

import android.arch.lifecycle.LiveData
import com.example.protiuc.sunshinekotlin.AppExecutors
import com.example.protiuc.sunshinekotlin.data.database.WeatherDao
import com.example.protiuc.sunshinekotlin.data.database.WeatherEntry
import com.example.protiuc.sunshinekotlin.data.model.Weather
import com.example.protiuc.sunshinekotlin.data.network.*
import com.example.protiuc.sunshinekotlin.utils.RateLimiter
import com.example.protiuc.sunshinekotlin.utils.SunshineDateUtils
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class SunshineRepository @Inject constructor(
        private val weatherService: WeatherService,
        private val weatherDao: WeatherDao,
        private val executors: AppExecutors) {

    private val forecastRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    private fun getForecast(query: String, format: String, units: String): LiveData<Resource<List<WeatherEntry>>> {
        val key = "forecastData"
        return object : NetworkBoundResource<List<WeatherEntry>, Weather>(executors) {
            override fun saveCallResult(item: Weather) {

                /*
                 * OWM returns daily forecasts based upon the local time of the city that is being asked
                 * for, which means that we need to know the GMT offset to translate this data properly.
                 * Since this data is also sent in-order and the first day is always the current day, we're
                 * going to take advantage of that to get a nice normalized UTC date for all of our weather.
                */
                val normalizedUtcStartDay = SunshineDateUtils.normalizedUtcMsForToday

                val databaseWeatherEntries = ArrayList<WeatherEntry>()
                item.list?.forEachIndexed { index, currentItem ->
                    // Create the weather entry object
                    val dateTimeMillis = normalizedUtcStartDay + SunshineDateUtils.DAY_IN_MILLIS * index
                    val weather = WeatherEntry(
                            currentItem.weatherDetails[0].id,
                            Date(dateTimeMillis),
                            currentItem.temp.min,
                            currentItem.temp.max,
                            currentItem.humidity,
                            currentItem.pressure,
                            currentItem.speed,
                            currentItem.deg)

                    databaseWeatherEntries.add(weather)
                }
                weatherDao.bulkInsert(databaseWeatherEntries.toTypedArray())
                Timber.d("Data saved into db")
            }

            override fun shouldFetch(data: List<WeatherEntry>?): Boolean {
               Timber.d("Should fetch data")
                 return data == null || data.isEmpty() || forecastRateLimit.shouldFetch(key)
            }

            override fun loadFromDb() = weatherDao.getCurrentWeatherForecasts(Date())

            override fun createCall() = weatherService.getWeatherForLocation(query, format, units, 14.toString())

            override fun onFetchFailed() {
                Timber.e("Fetch failed")
                forecastRateLimit.reset(key)
            }
        }.asLiveData()
    }

    fun getCurrentWeatherForecasts(): LiveData<Resource<List<WeatherEntry>>> {
        return getForecast("Mountain View, CA", "json", "metric")
    }

    private fun deleteOldData() {
        val today = SunshineDateUtils.normalizedUtcDateForToday
        weatherDao.deleteOldData(today)
    }
}