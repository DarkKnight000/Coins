package com.example.coins.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailItemResponse(
    val id_coin: Int,
    val country: String?,
    val collection_series: String?,
    val name: String?,
    val type: String?,
    val value: String?,
    val mint: String?,
    val year_century: String?,
    val material: String?,
    val mintage: Long?,
    val weight: Double?,
    val diameter: Double?,
    val coin_condition: String?,
    val purchase_price: Double?,
    val add_expenses: Double?,
    val purchase_date: String?,
    val count: Int,
    val description: String?,
//    val full_collection: Int?
)