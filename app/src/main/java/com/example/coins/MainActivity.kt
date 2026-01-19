package com.example.coins

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coins.data.ApiService
import com.example.coins.data.CoinViewModel
import com.example.coins.ui.theme.CoinsTheme
import com.example.coins.ui.screens.CoinScreen

class MainActivity : ComponentActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = ApiService()

        enableEdgeToEdge()
        setContent {
            CoinsTheme {
                // Создаём ViewModel с передачей apiService
                val viewModel: CoinViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            if (modelClass.isAssignableFrom(CoinViewModel::class.java)) {
                                @Suppress("UNCHECKED_CAST")
                                return CoinViewModel(apiService) as T
                            }
                            throw IllegalArgumentException("Unknown ViewModel class")
                        }
                    }
                )

                // Передаём ViewModel в UI
                CoinScreen(viewModel = viewModel)
            }
        }
    }
}
