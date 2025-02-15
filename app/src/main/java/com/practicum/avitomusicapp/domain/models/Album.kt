package com.practicum.avitomusicapp.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val id: Long? = null,
    val title: String?,
    val cover: String?
): Parcelable