package com.example.coins.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CoinItemResponse(
    val id_coin: Int,
    val country: String?,
    val collection_series: String?,
    val name: String?,
    val value: String?,
    val year_century: String?,
    val count: Int,
    val coin_condition: String?,
)