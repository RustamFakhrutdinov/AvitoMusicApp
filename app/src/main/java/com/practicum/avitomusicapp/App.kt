package com.practicum.avitomusicapp

import android.app.Application
import com.markodevcic.peko.PermissionRequester
import com.practicum.avitomusicapp.di.dataModule
import com.practicum.avitomusicapp.di.domainModule
import com.practicum.avitomusicapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        PermissionRequester.initialize(applicationContext)
        // Инициализация Koin
        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                domainModule,
                viewModelModule
            )
        }
    }
}