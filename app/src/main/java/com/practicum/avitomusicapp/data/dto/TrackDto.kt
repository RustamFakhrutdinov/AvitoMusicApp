package com.practicum.avitomusicapp.data.dto

import com.practicum.avitomusicapp.domain.models.Artist

data class TrackDto(
    val id: Long? = null,               // id трека
    val title: String? = null,          // Название композиции
    val duration: Int? = null,      // Продолжительность трека
    val md5_image: String? = null,      // Ссылка на изображение обложки
    val preview: String? = null,           // ссылка на отрывок трека
    val artist: Artist? = null         // Исполнитель
)