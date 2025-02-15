package com.practicum.avitomusicapp.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val id: Long? = null,               // id трека
    val title: String,          // Название композиции
    val duration: String,      // Продолжительность трека
    val image_url: String,      // Ссылка на изображение обложки
    val preview: String,           // ссылка на отрывок трека
    val artist: Artist,         // Исполнитель
    val album: Album
) : Parcelable {
    //fun getCover512() = image_url.replaceAfterLast('/',"512x512.jpg")
    fun getCover56() = image_url.replaceAfterLast('/', "56x56.jpg")
}