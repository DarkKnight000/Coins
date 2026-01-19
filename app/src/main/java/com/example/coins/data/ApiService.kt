package com.example.coins.data

import com.example.coins.data.model.response.ApiResponse
import com.example.coins.data.model.response.CoinCollectionResponse
import com.example.coins.data.model.response.CoinCountriesResponse
import com.example.coins.data.model.response.CoinDetailResponse
import com.example.coins.data.model.response.CoinsCountResponse
import com.example.coins.data.model.response.ImagesResponse
import com.example.coins.data.model.response.PaginatedCoinResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.net.ConnectException

class ApiService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true  // Преобразует null → default (если есть)
                    isLenient = true          // Игнорирует ошибки форматирования
                }
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 3000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 3000
        }

    }


//    private var host = "http://8ab05b7a3063.hosting.myjino.ru"
    private var host = "http://192.168.0.172"
//    private var host = "http://localhost"
    private var port = "5000"
    val pageSize = 10       // Количество монет для загрузки/отображения на странице

    /**
    Получить список монет
     **/
    suspend fun getCoins(
        countryId: Int?,
        collectionId: Int?,
        page: Int = 1,
        pageSize: Int = this.pageSize,
        searchText: String?
    ): PaginatedCoinResponse {
        return try {
            client.get("$host:$port/get_coins") {
                parameter("country_id1", countryId)
                parameter("collection_id", collectionId)
                parameter("page", page)
                parameter("page_size", pageSize)
                parameter("search_text", searchText)
            }.body<PaginatedCoinResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
    //            error("Ошибка загрузки данных")
    //            error(e.message ?: "Ошибка загрузки данных")
        } as PaginatedCoinResponse
    }

    /**
    Получить количество монет
     **/
    suspend fun getCoinsCount(
        countryId: Int?,
        collectionId: Int?,
        searchText: String?
    ): CoinsCountResponse {
        return try {
            client.get("$host:$port/get_coins_count") {
                parameter("country_id", countryId)
                parameter("collection_id", collectionId)
                parameter("search_text", searchText)
            }.body<CoinsCountResponse>()

        } catch (e: ConnectException) {
            e.printStackTrace()
    //            CoinsCountResponse(total_count = 0, unic_count = 0, total_price = 0.0, numista_price = 0.0)
        } as CoinsCountResponse
//        catch (e: SocketTimeoutException) {
//            e.printStackTrace()
//            CoinsCountResponse(total_count = 0, unic_count = 0, total_price = 0.0, numista_price = 0.0)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            CoinsCountResponse(total_count = 0, unic_count = 0, total_price = 0.0, numista_price = 0.0)
//        }
    }

    /**
    Получить список стран
     **/
    suspend fun getCountries(): List<CoinCountriesResponse> {
        return try {
            client.get("$host:$port/get_countries")
                .body<ApiResponse<List<CoinCountriesResponse>>>().data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
    Получить список коллекций
     **/
    suspend fun getCollections(): List<CoinCollectionResponse> {
        return try {
            client.get("$host:$port/get_collections_series")
                .body<ApiResponse<List<CoinCollectionResponse>>>().data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
    Получить информацию о монете
     **/
    suspend fun getCoinInfo(coinId: Int): CoinDetailResponse? {
        return try {
            client.get("$host:$port/get_coin_info/$coinId").body<CoinDetailResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
    Получить изображение монеты
     **/
    suspend fun getCoinImages(coinId: Int): ImagesResponse? {
        return try {
            client.get("$host:$port/coins/$coinId/images").body<ImagesResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}