package com.practicum.avitomusicapp.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artist(
    val id: Long? = null,
    val name: String
): Parcelable
