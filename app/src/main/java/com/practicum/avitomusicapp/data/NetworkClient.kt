package com.practicum.avitomusicapp.data

import com.practicum.avitomusicapp.data.dto.Response

interface NetworkClient {
     suspend fun doRequest(dto: Any): Response
}