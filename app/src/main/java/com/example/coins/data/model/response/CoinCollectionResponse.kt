package com.example.coins.data.model.response

import kotlinx.serialization.Serializable

@Serializable
class CoinCollectionResponse (
    val id_collection_series: Int,
    val collection_series: String,
    val total_coins_in_collection: Int?,
    val full_collection: Int?,
    val id_country: Int?,
)