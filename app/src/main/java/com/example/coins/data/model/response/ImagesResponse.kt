package com.example.coins.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ImagesResponse(
    val coin_id: Int,
    val images: List<String>
)