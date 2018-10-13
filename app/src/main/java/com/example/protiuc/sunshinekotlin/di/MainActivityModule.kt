package com.example.protiuc.sunshinekotlin.di

import com.example.protiuc.sunshinekotlin.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentsBuilderModule::class])
    abstract fun contributeMainActivity(): MainActivity
}