package com.example.coins.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.text.SimpleDateFormat
import java.util.Locale

object BitmapUtils {
    fun decodeBase64ToBitmap(base64String: String): Bitmap? = try {
        val data = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(data, 0, data.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

}

object DateUtils {
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun format(dateString: String): String = runCatching {
        inputFormat.parse(dateString)?.let { outputFormat.format(it) } ?: dateString
    }.getOrElse { dateString }
}