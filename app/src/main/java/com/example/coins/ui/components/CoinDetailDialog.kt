package com.example.coins.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coins.data.model.response.CoinDetailItemResponse
import com.example.coins.utils.DateUtils

@Composable
fun CoinDetailDialog(
    coinDetail: CoinDetailItemResponse?,
    coinImages: List<Bitmap>, // Новые изображения из CoinItem
    isLoading: Boolean,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = {
            Text(
                text = "Информация о монете",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Загрузка...",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                }
            } else if (coinDetail != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        // === Блок изображений (дублируем из CoinItem) ===
                        if (coinImages.isNotEmpty()) {
                            val pagerState = rememberPagerState(pageCount = { coinImages.size })
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxSize()
                                ) { page ->
                                    Image(
                                        bitmap = coinImages[page].asImageBitmap(),
                                        contentDescription = "Изображение монеты",
                                        contentScale = ContentScale.FillWidth,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                // Индикатор страниц
                                if (coinImages.size > 1) {
                                    HorizontalPagerIndicator(
                                        pagerState = pagerState,
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(10.dp),
                                        activeColor = MaterialTheme.colorScheme.onSurface,
                                        indicatorWidth = 12.dp,
                                        indicatorHeight = 3.dp
                                    )
                                }

                                // Счетчик "2/5"
                                if (coinImages.size > 1) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${pagerState.currentPage + 1}/${coinImages.size}",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        else { /* Заглушка */}

                    }

                    // Основная информация
                    item {
                        if (coinDetail.name != null) {
                            DetailItem("Название", coinDetail.name)
                        }
                        if (coinDetail.country != null) {
                            DetailItem("Страна", coinDetail.country)
                        }
                        if (coinDetail.collection_series != null) {
                            DetailItem("Коллекция,\nсерия", coinDetail.collection_series)
                        }
                        if (coinDetail.type != null) {
                            DetailItem("Тип", coinDetail.type)
                        }
                        if (coinDetail.value != null) {
                            DetailItem("Номинал", coinDetail.value)
                        }
                        if (coinDetail.year_century != null) {
                            DetailItem("Год", coinDetail.year_century)
                        }
                        if (coinDetail.mint != null) {
                            DetailItem("Монетный двор", coinDetail.mint)
                        }
                        if (coinDetail.material != null) {
                            DetailItem("Материал", coinDetail.material)
                        }
                        if (coinDetail.mintage != null) {
                            DetailItem("Тираж", coinDetail.mintage.toString())
                        }
                        if (coinDetail.weight != null) {
                            DetailItem("Вес", "${coinDetail.weight} г")
                        }
                        if (coinDetail.diameter != null) {
                            DetailItem("Диаметр", "${coinDetail.diameter} мм")
                        }
                        if (coinDetail.coin_condition != null) {
                            DetailItem("Состояние", coinDetail.coin_condition)
                        }
                        if (coinDetail.purchase_price != null) {
                            DetailItem("Цена покупки", "${coinDetail.purchase_price.toInt()} ₽")
                        }
                        if (coinDetail.add_expenses != null) {
                            DetailItem("Доп. расходы", "${coinDetail.add_expenses.toInt()} ₽")
                        }
                        if (coinDetail.purchase_date != null) {
                            DetailItem(
                                "Дата покупки",
                                DateUtils.format(coinDetail.purchase_date)
                            )
                        }
                        if (coinDetail.count.toString() != null) {
                            DetailItem("Количество", coinDetail.count.toString())
                        }
                        if (coinDetail.description != null) {
                            DetailItem("Опмсание", coinDetail.description)
                        }
                    }
                }
            } else {
                Text(
                    text = "Не удалось загрузить информацию о монете.",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Закрыть", fontWeight = FontWeight.SemiBold)
            }
        },
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 8.dp,
        modifier = Modifier.padding(horizontal = 0.dp, vertical = 50.dp)
    )
}