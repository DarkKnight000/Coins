package com.example.coins.data.model

import android.graphics.Bitmap

data class Coin(
    val idCoin: Int,
    val country: String?,
    val collection: String?,
    val year_century: String?,
    val title: String?,
    val value: String?,
    val count: Int,
    val imageBitmaps: List<Bitmap> = emptyList(),
    val coin_condition: String?
//    val imageUrls: List<String>

//    val imageBase64List: List<String>,
)