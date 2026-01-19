package com.example.coins.data.model.response

import kotlinx.serialization.Serializable

@Serializable
class CoinCountriesResponse (
    val id_country: Int,
    val country: String
)