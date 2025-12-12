package com.example.smartshopapp.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smartshopapp.data.remote.ProductRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    repository: ProductRepository
) {
    val scope = rememberCoroutineScope()

    // Observed states
    var totalProducts by remember { mutableStateOf(0) }
    var totalStock by remember { mutableStateOf(0) }
    var avgPrice by remember { mutableStateOf(0.0) }
    var maxPriceProduct by remember { mutableStateOf<String?>(null) }

    var chartData by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    // Load statistics once
    LaunchedEffect(Unit) {
        val products = repository.getAllProductsOnce()

        totalProducts = products.size
        totalStock = products.sumOf { it.quantity }
        avgPrice = if (products.isNotEmpty()) products.map { it.price }.average() else 0.0
        maxPriceProduct = products.maxByOrNull { it.price }?.name

        // map for bar chart: product name → stock quantity
        chartData = products.map { it.name to it.quantity }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Statistics") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // ---------- STATS CARDS ----------
            StatCard("Total Products", totalProducts.toString())
            StatCard("Total Stock", totalStock.toString())
            StatCard("Average Price", String.format("%.2f", avgPrice) + " dt")
            StatCard("Most Expensive Product", maxPriceProduct ?: "N/A")

            Spacer(Modifier.height(30.dp))

            Text(
                text = "Stock Distribution",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(10.dp))

            // ---------- SIMPLE BAR CHART ----------
            BarChart(data = chartData)
        }
    }
}
