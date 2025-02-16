package com.practicum.avitomusicapp.di

import com.practicum.avitomusicapp.domain.downloads.DownloadsInteractor
import com.practicum.avitomusicapp.domain.downloads.impl.DownloadsInteractorImpl
import com.practicum.avitomusicapp.domain.player.PlayerInteractor
import com.practicum.avitomusicapp.domain.player.impl.PlayerInteractorImpl
import com.practicum.avitomusicapp.domain.search.api.TracksInteractor
import com.practicum.avitomusicapp.domain.search.impl.TracksInteractorImpl
import org.koin.dsl.module

val domainModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory <PlayerInteractor>{
        PlayerInteractorImpl(get())
    }

    single <DownloadsInteractor>{
        DownloadsInteractorImpl(get())
    }

}