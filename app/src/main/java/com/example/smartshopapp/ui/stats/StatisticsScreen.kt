package com.example.smartshopapp.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smartshopapp.data.remote.ProductRepository
import com.example.smartshopapp.ui.theme.OldRose
import com.example.smartshopapp.ui.theme.SpaceIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    repository: ProductRepository,
    onBack: () -> Unit
) {
    var totalProducts by remember { mutableStateOf(0) }
    var totalStock by remember { mutableStateOf(0) }
    var avgPrice by remember { mutableStateOf(0.0) }
    var maxPriceProduct by remember { mutableStateOf<String?>(null) }
    var chartData by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    LaunchedEffect(Unit) {
        val products = repository.getAllProductsOnce()

        totalProducts = products.size
        totalStock = products.sumOf { it.quantity }
        avgPrice = if (products.isNotEmpty()) products.map { it.price }.average() else 0.0
        maxPriceProduct = products.maxByOrNull { it.price }?.name
        chartData = products.map { it.name to it.quantity }
    }

    Scaffold(
        containerColor = OldRose,
        topBar = {
            TopAppBar(
                title = { Text("Statistics", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OldRose)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------- SUMMARY CARDS ----------
            StatCard(title = "Total Products", value = totalProducts.toString())
            StatCard(title = "Total Stock", value = totalStock.toString())
            StatCard(title = "Average Price", value = "%.2f DT".format(avgPrice))
            StatCard(title = "Most Expensive", value = maxPriceProduct ?: "N/A")

            Spacer(Modifier.height(10.dp))

            // ---------- CHART ----------
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Stock Distribution",
                        style = MaterialTheme.typography.titleMedium,
                        color = SpaceIndigo
                    )

                    BarChart(
                        data = chartData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }
    }
}
