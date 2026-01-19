package com.example.coins.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedCoinResponse(
    val data: List<CoinItemResponse>,
    val page: Int,
    val page_size: Int
)