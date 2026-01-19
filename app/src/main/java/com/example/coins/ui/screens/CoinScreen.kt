package com.example.coins.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coins.data.CoinViewModel
import com.example.coins.ui.components.CoinDetailDialog
import com.example.coins.ui.components.CoinItem
import com.example.coins.ui.components.CountryDropdown
import com.example.coins.ui.components.CollectionDropdown
import kotlinx.coroutines.launch

import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.flow.distinctUntilChanged

import androidx.compose.material.icons.filled.Search

/**
    Основной экран приложения "Монеты".
    Отображает список монет с фильтрами, поиском и возможностью просмотра деталей.

*   @param viewModel ViewModel, управляющий данными и логикой
**/
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CoinScreen(viewModel: CoinViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Состояние pull-to-refresh
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.refreshData() }
    )

    // Следим за изменением индекса первой видимой строки и уведомляем ViewModel, чтобы показать/скрыть кнопку "Наверх"
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { viewModel.onScroll(it) }
    }

    // Автоматическая подгрузка следующей страницы при приближении к концу списка
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            // Подгружаем, когда остаётся 3 элемента до конца
            lastVisibleItem >= layoutInfo.totalItemsCount - 3
        }.distinctUntilChanged()
            .collect { if (it) viewModel.onLastItemReached() }
    }

    Scaffold(
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Основной список монет с пагинацией и обновлением
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(refreshState) // Поддержка свайпа вниз
                        .background(Color.LightGray),
                    contentPadding = PaddingValues(bottom = 80.dp, top = 60.dp)
                ) {
                    // Элемент с фильтрами (страна и коллекция)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            // Выпадающий список стран
                            if (viewModel.countries.isNotEmpty()) {
                                Text(
                                    text = "Страна",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 0.dp)
                                )
                                CountryDropdown(
                                    countries = viewModel.countries,
                                    selectedCountry = viewModel.selectedCountry,
                                    onCountrySelected = { newSelection ->
                                        viewModel.selectCountry(newSelection)
                                    }
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                            }

                            // Выпадающий список коллекций (фильтруется по стране)
                            if (viewModel.collections.isNotEmpty()) {
                                Text(
                                    text = "Коллекция",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 0.dp)
                                )
                                CollectionDropdown(
                                    collections = viewModel.filteredCollections,
                                    selectedCollection = viewModel.selectedCollection,
                                    onCollectionSelected = { newSelection ->
                                        viewModel.selectCollection(newSelection)
                                    }
                                )
                            }
                        }
                    }

                    // Показываем сообщение об ошибке, если есть
//                    if (viewModel.errorMessage != null) {
//                        item {
//                            ErrorBanner(
//                                message = viewModel.errorMessage!!,
//                                onDismiss = { viewModel.clearError() }
//                            )
//                        }
//                    }

                    // Элемент с отображением общего количества монет
                    item {
                        Text(
                            text = "Всего монет: ${viewModel.totalCount} (уникальных: ${viewModel.unicCount})",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Затраты
                    item {
                        Text(
                            text = "Затраты на текущие монеты: ${viewModel.totalPrice} ₽",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Рыночная стоимость текуцщих монет
                    item {
                        Text(
                            text = "Рыночная стоимость текущих монет: ${viewModel.numistaPrice} ₽",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Список монет
                    items(viewModel.coins) { coin ->
                        CoinItem(
                            coin = coin
                        ) {
                            viewModel.openCoinDetail(coin)
                        }
                    }

                    // Индикатор загрузки следующей страницы
                    if (viewModel.isLoading)
                    {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Загрузка...",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                // Индикатор обновления при свайпе вниз (Pull-to-refresh)
                PullRefreshIndicator(
                    refreshing = viewModel.isRefreshing,
                    state = refreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = MaterialTheme.colorScheme.primary
                )

                // Кнопка "Наверх"
                AnimatedVisibility(
                    visible = viewModel.showScrollToTop,
                    enter = fadeIn(tween(300)) + scaleIn(tween(300)),
                    exit = fadeOut(tween(300)) + scaleOut(tween(300)),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.scrollToTop()
                            coroutineScope.launch { listState.animateScrollToItem(0) }
                        },
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.ArrowUpward, "Наверх")
                    }
                }

                // Поисковая строка
                SearchBar(viewModel)
            }

            // Диалог с детальной информацией о монете
            viewModel.selectedCoin?.let {
                CoinDetailDialog(
                    coinDetail = viewModel.coinDetail,
                    coinImages = it.imageBitmaps,
                    isLoading = viewModel.detailLoading,
                    onDismiss = { viewModel.closeCoinDetail() }
                )
            }
        }
    )
}

//@Composable
//private fun ErrorBanner(
//    message: String,
//    onDismiss: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = Icons.Default.Error,
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.onErrorContainer,
//                modifier = Modifier.size(24.dp)
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Text(
//                text = message,
//                color = MaterialTheme.colorScheme.onErrorContainer,
//                modifier = Modifier.weight(1f)
//            )
//            IconButton(onClick = onDismiss) {
//                Icon(
//                    imageVector = Icons.Default.Close,
//                    contentDescription = "Закрыть",
//                    tint = MaterialTheme.colorScheme.onErrorContainer
//                )
//            }
//        }
//    }
//}


/**
    Компонент поисковой строки.
    Позволяет вводить текст и искать монеты.

*   @param viewModel ViewModel для передачи текста поиска
**/
@Composable
private fun SearchBar(viewModel: CoinViewModel) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchText by rememberSaveable { mutableStateOf("") }

    Box()
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .clickable {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
        ) {
            // Поле ввода поиска
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                ) {
                    // Иконка поиска
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    // Поле ввода
                    Box(Modifier.weight(1f)) {
                        if (searchText.isEmpty()) {
                            Text(
                                "Введите запрос",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                            )
                        }
                        BasicTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .focusable(),
                            singleLine = true,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            decorationBox = { inner -> inner() }
                        )
                    }

                    // Кнопка поиска
                    IconButton(
                        onClick = {
                            viewModel.setSearchText(searchText)
                            keyboardController?.hide()
                        },
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Найти",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}