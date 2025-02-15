package com.practicum.avitomusicapp.di

import com.practicum.avitomusicapp.ui.player.PlayerViewModel
import com.practicum.avitomusicapp.ui.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(get(),get())
    }

    viewModel {
        PlayerViewModel(get(),get())
    }
}