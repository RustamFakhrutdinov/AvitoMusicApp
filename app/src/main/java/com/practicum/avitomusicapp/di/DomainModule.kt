package com.practicum.avitomusicapp.di

import com.practicum.avitomusicapp.domain.player.PlayerInteractor
import com.practicum.avitomusicapp.domain.player.impl.PlayerInteractorImpl
import com.practicum.avitomusicapp.domain.search.api.TracksInteractor
import com.practicum.avitomusicapp.domain.search.impl.TracksInteractorImpl
import org.koin.dsl.module

val domainModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single <PlayerInteractor>{
        PlayerInteractorImpl(get())
    }

}