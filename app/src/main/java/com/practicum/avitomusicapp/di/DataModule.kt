package com.practicum.avitomusicapp.di

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.avitomusicapp.data.NetworkClient
import com.practicum.avitomusicapp.data.network.DeezerApi
import com.practicum.avitomusicapp.data.network.RetrofitNetworkClient
import com.practicum.avitomusicapp.data.player.PlayerRepositoryImpl
import com.practicum.avitomusicapp.data.search.TracksRepositoryImpl
import com.practicum.avitomusicapp.domain.player.PlayerRepository
import com.practicum.avitomusicapp.domain.search.api.TracksRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.deezer.com/"


val dataModule = module {

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeezerApi::class.java)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    factory {
        MediaPlayer()
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single <PlayerRepository>{
        PlayerRepositoryImpl(get())
    }
}