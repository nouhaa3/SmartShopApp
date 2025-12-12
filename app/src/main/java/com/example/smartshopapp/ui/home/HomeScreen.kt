@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.smartshopapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onProductsClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("SmartShop Dashboard") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
        ) {
            Button(onClick = onProductsClick, modifier = Modifier.fillMaxWidth()) {
                Text("Products")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onStatsClick, modifier = Modifier.fillMaxWidth()) {
                Text("Statistics")
            }
        }
    }
}

