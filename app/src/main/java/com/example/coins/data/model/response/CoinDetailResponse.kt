package com.example.coins.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailResponse(
    val data: CoinDetailItemResponse,
    val count: Int
)