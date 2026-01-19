package com.example.coins.data.model.response

import kotlinx.serialization.Serializable

// Вспомогательная модель для обёртки
@Serializable
data class ApiResponse<T>(val data: T)