package com.example.smartshopapp.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smartshopapp.domain.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: ProductViewModel
) {
    val state = viewModel.uiState.collectAsState().value

    val totalProducts = state.products.size
    val totalValue = state.products.sumOf { it.quantity * it.price }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // -------------------------
            // CARDS
            // -------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF7FF))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Products", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "$totalProducts",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2FFE7))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Stock Value", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${"%.2f".format(totalValue)} DT",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "Stock by Product",
                style = MaterialTheme.typography.titleMedium
            )

            // -------------------------------------
            // BAR CHART (simple visualization)
            // -------------------------------------
            BarChart(products = state.products)
        }
    }
}

@Composable
fun BarChart(products: List<com.example.smartshopapp.data.local.ProductEntity>) {
    if (products.isEmpty()) {
        Text("No data to display")
        return
    }

    val maxQuantity = products.maxOf { it.quantity }.coerceAtLeast(1)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(10.dp)
    ) {
        val barWidth = size.width / (products.size * 2)

        products.forEachIndexed { index, product ->
            val barHeight = (product.quantity.toFloat() / maxQuantity) * size.height

            drawRect(
                color = Color(0xFF4A90E2),
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = index * barWidth * 2,
                    y = size.height - barHeight
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = barWidth,
                    height = barHeight
                )
            )
        }
    }
}
