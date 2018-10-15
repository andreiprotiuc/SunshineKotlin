package com.example.protiuc.sunshinekotlin.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.protiuc.sunshinekotlin.ui.main.MainViewModel
import com.example.protiuc.sunshinekotlin.ui.main.MainViewModelFactory
import dagger.Binds
import dagger.Module

@Module
@Suppress("unused")
abstract class ViewModelModule {
    @Binds
    abstract fun bindMainViewModel(userViewModel: MainViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: MainViewModelFactory): ViewModelProvider.Factory
}