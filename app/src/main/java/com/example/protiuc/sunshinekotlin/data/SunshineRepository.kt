package com.example.protiuc.sunshinekotlin.data

import android.arch.lifecycle.LiveData
import android.util.Log
import com.example.protiuc.sunshinekotlin.AppExecutors
import com.example.protiuc.sunshinekotlin.data.database.WeatherDao
import com.example.protiuc.sunshinekotlin.data.database.WeatherEntry
import com.example.protiuc.sunshinekotlin.data.model.Weather
import com.example.protiuc.sunshinekotlin.data.network.*
import com.example.protiuc.sunshinekotlin.utils.SunshineDateUtils
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class SunshineRepository @Inject constructor(
        private val weatherService: WeatherService,
        private val weatherDao: WeatherDao,
        private val executors: AppExecutors) {

    private fun getForecast(query: String, format: String, units: String): LiveData<Resource<List<WeatherEntry>>> {
        weatherService.getWeatherForLocation(query, format, units, 14.toString())
        return object : NetworkBoundResource<List<WeatherEntry>, Weather>(executors) {
            override fun saveCallResult(item: Weather) {

                /*
    * OWM returns daily forecasts based upon the local time of the city that is being asked
    * for, which means that we need to know the GMT offset to translate this data properly.
    * Since this data is also sent in-order and the first day is always the current day, we're
    * going to take advantage of that to get a nice normalized UTC date for all of our weather.
    */
                val normalizedUtcStartDay = SunshineDateUtils.normalizedUtcMsForToday

                val databaseWeatherEntries =  ArrayList<WeatherEntry>()
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
                Log.d("ceva", "saveCallResult")
            }

            override fun shouldFetch(data: List<WeatherEntry>?): Boolean {
                Log.d("ceva", "shouldFetch")
                // return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(owner)
                return true
            }

            override fun loadFromDb() = weatherDao.getCurrentWeatherForecasts(Date())

            override fun createCall() = weatherService.getWeatherForLocation(query, format, units, 14.toString())

            override fun onFetchFailed() {
                Log.d("ceva", "failed")
                //repoListRateLimit.reset(owner)
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