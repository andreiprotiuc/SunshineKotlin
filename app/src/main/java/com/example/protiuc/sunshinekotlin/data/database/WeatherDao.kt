package com.example.protiuc.sunshinekotlin.data.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import java.util.*

@Dao
interface WeatherDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun bulkInsert(weather: Array<WeatherEntry>)

    @Query("SELECT * FROM weather WHERE date = :date")
    fun getWeatherByDate(date: Date): LiveData<WeatherEntry>

    @Query("SELECT COUNT(id) FROM weather WHERE date >= :date ")
    fun countAllFutureWeather(date: Date): Int

    @Query("DELETE FROM weather WHERE date <= :date")
    fun deleteOldData(date: Date)

    @Query("SELECT * FROM weather WHERE date >= :date")
    fun getCurrentWeatherForecasts(date: Date): LiveData<List<WeatherEntry>>
}