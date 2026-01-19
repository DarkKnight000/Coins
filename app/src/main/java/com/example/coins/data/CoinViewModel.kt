package com.example.coins.data

//import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coins.data.model.Coin
import com.example.coins.data.model.response.CoinCollectionResponse
import com.example.coins.data.model.response.CoinCountriesResponse
import com.example.coins.data.model.response.CoinDetailItemResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
    ViewModel для управления данными монет в приложении.
    Содержит логику загрузки, фильтрации и отображения монет.
**/
class CoinViewModel(private val apiService: ApiService) : ViewModel() {

    // Состояния
    var totalCount by mutableIntStateOf(0); private set // Общее количество монет
    var unicCount by mutableIntStateOf(0); private set // Количество уникальных монет
    var totalPrice: Double by mutableDoubleStateOf(0.0); private set // Затраты на монеты
    var numistaPrice: Double by mutableDoubleStateOf(0.0); private set // Рыночная стоимость монет
    var countries by mutableStateOf<List<CoinCountriesResponse>>(emptyList()); private set // Список стран
    var collections by mutableStateOf<List<CoinCollectionResponse>>(emptyList()); private set // Список коллекций
    var coins by mutableStateOf<List<Coin>>(emptyList()); private set // Список монет для отображения
    var selectedCoin: Coin? by mutableStateOf(null); private set // Выбранная монета для деталей
    var coinDetail: CoinDetailItemResponse? by mutableStateOf(null); private set // Детальная информация о монете
    var detailLoading by mutableStateOf(false); private set // Флаг загрузки деталей монеты
    var isRefreshing by mutableStateOf(false); private set // Флаг обновления данных (pull-to-refresh)
    var isLoading by mutableStateOf(false); private set // Флаг подгрузки следующей страницы
    var isLastPage by mutableStateOf(false); private set // Флаг: достигнута последняя страница
    var showScrollToTop by mutableStateOf(false); private set // Показывать кнопку "наверх"
//    var errorMessage: String? by mutableStateOf(null); private set

    // Внутренние состояния
    private var currentPage by mutableIntStateOf(1) // Текущая страница пагинации
    private val loadingImages = mutableSetOf<Int>() // Множество ID монет, у которых идёт загрузка изображений

    // Фильтры
    var selectedCountry: CoinCountriesResponse? by mutableStateOf(null); private set // Выбранная страна
    var selectedCollection: CoinCollectionResponse? by mutableStateOf(null); private set // Выбранная коллекция
    var appliedSearchText: String? by mutableStateOf(null); private set // Текст поискового запроса

    init {
        loadInitialData()
    }

    /**
        Загрузка начальных данных: счётчиков, стран, коллекций и первой страницы монет.
    **/
    private fun loadInitialData() {
        viewModelScope.launch {
//            errorMessage = null
            try {
                // Запускаем параллельные запросы
                val countJob = async { apiService.getCoinsCount(null, null, null) }
                val countriesJob = async { apiService.getCountries() }
                val collectionsJob = async { apiService.getCollections() }

                // Ожидаем результаты
                val countResponse = countJob.await()
                totalCount = countResponse.total_count
                unicCount = countResponse.unic_count
                totalPrice = countResponse.total_price
                numistaPrice = countResponse.numista_price

                countries = countriesJob.await()
                collections = collectionsJob.await()

                // Загружаем первую страницу монет
                loadCoinsPage()
            } catch (e: Exception) {
//                errorMessage = e.message ?: "Ошибка загрузки данных"
                e.printStackTrace()
            }
        }
    }

    /**
        Обновление данных при свайпе вниз (pull-to-refresh).
        Перезагружает справочники, счётчики и первую страницу монет.
    **/
    fun refreshData() {
        viewModelScope.launch {
            isRefreshing = true
//            errorMessage = null
            try {
                val (countryId, collectionId) = getFilterParams()

                // Обновляем счётчики
                val countResponse = apiService.getCoinsCount(
                    countryId = countryId,
                    collectionId = collectionId,
                    searchText = appliedSearchText
                )
                totalCount = countResponse.total_count
                unicCount = countResponse.unic_count
                totalPrice = countResponse.total_price
                numistaPrice = countResponse.numista_price

                // Перезагружаем справочники
                countries = apiService.getCountries()
                collections = apiService.getCollections()

                // Сбрасываем список и загружаем заново
                coins = emptyList()
                currentPage = 1
                isLastPage = false
                loadCoinsPage()
            } catch (e: Exception) {
//                errorMessage = e.message ?: "Ошибка обновления данных"
                e.printStackTrace()
            } finally {
                isRefreshing = false // Скрываем индикатор обновления
            }
        }
    }

    /**
        Загрузка следующей страницы монет (пагинация).
        Автоматически применяет текущие фильтры.
    **/
    fun loadCoinsPage() {
        if (isLoading || isLastPage) return // Защита от повторных вызовов
        isLoading = true
//        errorMessage = null

        viewModelScope.launch {
            try {
                val (countryId, collectionId) = getFilterParams()

                val response = apiService.getCoins(
                    countryId = countryId,
                    collectionId = collectionId,
                    page = currentPage,
                    pageSize = apiService.pageSize,
                    searchText = appliedSearchText
                )

                // Преобразуем данные из ответа в модель Coin
                val newCoins = response.data.map { item ->
                    Coin(
                        idCoin = item.id_coin,
                        country = item.country,
                        year_century = item.year_century,
                        collection = item.collection_series,
                        title = item.name,
                        value = item.value,
                        count = item.count,
                        imageBitmaps = emptyList(),
                        coin_condition = item.coin_condition
                    )
                }

                // Добавляем новые монеты к существующему списку
                coins = coins + newCoins

                // Проверяем, есть ли ещё данные для подгрузки
                if (response.data.size < apiService.pageSize) {
                    isLastPage = true
                } else {
                    currentPage++
                }

                // Асинхронно загружаем изображения для новых монет
                newCoins.forEach { coin ->
                    if (coin.idCoin !in loadingImages) {
                        loadingImages.add(coin.idCoin)
                        loadCoinImages(coin.idCoin)
                    }
                }
            } catch (e: Exception) {
//                errorMessage = e.message ?: "Ошибка загрузки монет"
                e.printStackTrace()
            } finally {
                isLoading = false // Снимаем флаг загрузки
            }
        }
    }

    /**
        Асинхронная загрузка изображений для монеты по её ID.
        Изображения декодируются из Base64.
    **/
    private fun loadCoinImages(coinId: Int) {
        viewModelScope.launch {
            try {
                val imagesResponse = apiService.getCoinImages(coinId)
                val bitmaps = imagesResponse?.images?.mapNotNull { base64 ->
                    com.example.coins.utils.BitmapUtils.decodeBase64ToBitmap(base64)
                } ?: emptyList()

                // Обновляем монету с загруженными изображениями
                coins = coins.map { if (it.idCoin == coinId) it.copy(imageBitmaps = bitmaps) else it }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loadingImages.remove(coinId)
            }
        }
    }

    /**
        Загрузка детальной информации о монете.
    **/
    fun loadCoinDetail(coinId: Int) {
        if (detailLoading) return
        detailLoading = true
        coinDetail = null
//        errorMessage = null

        viewModelScope.launch {
            try {
                val response = apiService.getCoinInfo(coinId)
                coinDetail = response?.data
            } catch (e: Exception) {
//                errorMessage = e.message ?: "Ошибка загрузки деталей"
                e.printStackTrace()
            } finally {
                detailLoading = false
            }
        }
    }


    // === Управление фильтрами ===

    /**
        Выбор страны фильтрации.
        Сбрасывает выбор коллекции и перезагружает данные.
    **/
    fun selectCountry(country: CoinCountriesResponse?) {
        selectedCountry = country
        selectedCollection = null
        resetAndReload()
    }

    /**
        Выбор коллекции фильтрации.
        Перезагружает данные.
    **/
    fun selectCollection(collection: CoinCollectionResponse?) {
        selectedCollection = collection
        resetAndReload()
    }

    /**
        Установка текста поиска.
        Сбрасывает фильтр, если текст пустой.
    **/
    fun setSearchText(text: String?) {
        appliedSearchText = text?.ifBlank { null }
        resetAndReload()
    }

    /**
        Сброс текущего списка и перезагрузка данных с актуальными фильтрами.
    **/
    private fun resetAndReload() {
        coins = emptyList()
        currentPage = 1
        isLastPage = false
        loadCoinsPage()
        updateCounts()
    }

    /**
        Асинхронное обновление счётчиков монет с учётом текущих фильтров.
    **/
    private fun updateCounts() {
        viewModelScope.launch {
//            errorMessage = null
            try {
                val (countryId, collectionId) = getFilterParams()
                val countResponse = apiService.getCoinsCount(
                    countryId = countryId,
                    collectionId = collectionId,
                    searchText = appliedSearchText
                )
                totalCount = countResponse.total_count
                unicCount = countResponse.unic_count
                totalPrice = countResponse.total_price
                numistaPrice = countResponse.numista_price
            } catch (e: Exception) {
//                errorMessage = e.message ?: "Ошибка обновления статистики"
                e.printStackTrace()
            }
        }
    }

    // === Вспомогательные функции ===

    /**
        Открытие диалога с деталями монеты.
    **/
    fun openCoinDetail(coin: Coin) {
        selectedCoin = coin
        loadCoinDetail(coin.idCoin)
    }

    /**
        Закрытие диалога с деталями монеты.
    **/
    fun closeCoinDetail() {
        selectedCoin = null
        coinDetail = null
    }

    /**
        Скрытие кнопки "наверх".
    **/
    fun scrollToTop() {
        showScrollToTop = false
    }

    /**
        Обработка прокрутки списка для отображения кнопки "наверх".
    **/
    fun onScroll(index: Int) {
        showScrollToTop = index > 1
    }

    /**
        Подгрузка следующей страницы при достижении конца списка.
    **/
    fun onLastItemReached() {
        if (!isLoading && !isLastPage) {
            loadCoinsPage()
        }
    }

    /**
        Получение параметров фильтрации в виде пары (countryId, collectionId).
        Игнорирует фильтры с ID = -1.
    **/
    private fun getFilterParams(): Pair<Int?, Int?> {
        val countryId = selectedCountry?.takeIf { it.id_country != -1 }?.id_country
        val collectionId = selectedCollection?.takeIf { it.id_collection_series != -1 }?.id_collection_series
        return countryId to collectionId
    }

    /**
        Отфильтрованный список коллекций, зависящий от выбранной страны.
        Используется для отображения только релевантных коллекций.
    **/
    val filteredCollections: List<CoinCollectionResponse> by derivedStateOf {
        selectedCountry?.let { country ->
            if (country.id_country == -1) collections
            else collections.filter { it.id_country == country.id_country || it.id_country == null }
        } ?: collections
    }

//    fun clearError() {
//        errorMessage = null
//    }
}