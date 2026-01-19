package com.example.coins.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.coins.data.model.response.CoinCollectionResponse
import com.example.coins.data.model.response.CoinCountriesResponse


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDropdown(
    countries: List<CoinCountriesResponse>,
    selectedCountry: CoinCountriesResponse?,
    onCountrySelected: (CoinCountriesResponse?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = selectedCountry?.country ?: "Все"

    val items = remember(countries) {
        listOf(CoinCountriesResponse(id_country = -1, country = "Все")) + countries
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_mapmode),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(0.6f)
            ),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Выберите страну") },
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .wrapContentHeight()
                .padding(0.dp)
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            items.forEach { country ->
                DropdownMenuItem(
                    modifier = Modifier.padding(0.dp),
                    text = {
                        Text(
                            text = country.country,
                            color = if (country.id_country == -1)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onCountrySelected(if (country.id_country == -1) null else country)
                        expanded = false
                    },
                    contentPadding = PaddingValues(16.dp),
                    trailingIcon = {
                        if (selectedCountry == country) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_gallery),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDropdown(
    collections: List<CoinCollectionResponse>,
    selectedCollection: CoinCollectionResponse?,
    onCollectionSelected: (CoinCollectionResponse?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = selectedCollection?.collection_series ?: "Все"

    val items = remember(collections) {
        listOf(
            CoinCollectionResponse(
                id_collection_series = -1,
                collection_series = "Все",
                total_coins_in_collection = null,
                full_collection = 0,
                id_country = -1
            )
        ) + collections
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_compass),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(0.6f)
            ),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Выберите коллекцию") },
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .wrapContentHeight()
                .padding(0.dp)
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            items.forEach { collection ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = collection.collection_series,
                            color = if (collection.id_collection_series == -1)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onCollectionSelected(if (collection.id_collection_series == -1) null else collection)
                        expanded = false
                    },
                    contentPadding = PaddingValues(16.dp),
                    trailingIcon = {
                        if (selectedCollection == collection) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_gallery),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}