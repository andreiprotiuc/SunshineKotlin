package com.example.protiuc.sunshinekotlin.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

@Database(entities = [WeatherEntry::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class SunshineDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}