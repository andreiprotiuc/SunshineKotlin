package com.example.protiuc.sunshinekotlin.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.example.protiuc.sunshinekotlin.AppExecutors
import com.example.protiuc.sunshinekotlin.data.SunshineRepository
import com.example.protiuc.sunshinekotlin.data.database.SunshineDatabase
import com.example.protiuc.sunshinekotlin.data.database.WeatherDao
import com.example.protiuc.sunshinekotlin.data.network.WeatherNetworkDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideApplicationContext(app: Application): Context {
        return app.applicationContext
    }

    @Singleton
    @Provides
    fun provideDatabase(app: Application): SunshineDatabase {
        return Room.databaseBuilder(app, SunshineDatabase::class.java, "weather").build()
    }

    @Singleton
    @Provides
    fun provideWeatherDao(db: SunshineDatabase): WeatherDao {
        return db.weatherDao()
    }

    @Singleton
    @Provides
    fun provideRepository(db: SunshineDatabase, executors: AppExecutors): SunshineRepository {
        val networkDataSource = WeatherNetworkDataSource(executors)
        return SunshineRepository(db.weatherDao(), networkDataSource, executors)
    }
}