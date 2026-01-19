package com.example.coins.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CoinsCountResponse(
    val total_count: Int,
    val unic_count: Int,
    val total_price: Double,
    val numista_price: Double,
)