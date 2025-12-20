package com.example.smartshopapp.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var highestPrice by remember { mutableStateOf(0.0) }
    var maxPriceProduct by remember { mutableStateOf<String?>(null) }
    var chartData by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    // LOAD DATA ONCE
    LaunchedEffect(Unit) {
        val products = repository.getAllProductsOnce()

        totalProducts = products.size
        totalStock = products.sumOf { it.quantity }
        avgPrice = if (products.isNotEmpty()) {
            products.map { it.price }.average()
        } else 0.0

        highestPrice = products.maxOfOrNull { it.price } ?: 0.0
        maxPriceProduct = products.maxByOrNull { it.price }?.name
        chartData = products.map { it.name to it.quantity }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        OldRose,
                        OldRose.copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Statistics",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }
                    },
                    navigationIcon = {
                        Surface(
                            onClick = onBack,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(44.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCard(
                        title = "Products",
                        value = totalProducts.toString(),
                        icon = "",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Stock",
                        value = totalStock.toString(),
                        icon = "",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCard(
                        title = "Avg Price",
                        value = "%.2f DT".format(avgPrice),
                        icon = "",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Highest",
                        value = "%.0f DT".format(highestPrice),
                        icon = "",
                        modifier = Modifier.weight(1f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                "Most Expensive",
                                fontSize = 13.sp,
                                color = SpaceIndigo.copy(alpha = 0.6f)
                            )
                            Text(
                                maxPriceProduct ?: "N/A",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpaceIndigo
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            "Stock Distribution",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpaceIndigo
                        )
                        Spacer(Modifier.height(16.dp))
                        BarChart(
                            data = chartData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )
                    }
                }
            }
        }
    }
}

/* ---------- STAT CARD ---------- */

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    title,
                    fontSize = 13.sp,
                    color = SpaceIndigo.copy(alpha = 0.6f)
                )
                Text(icon, fontSize = 20.sp)
            }
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OldRose
            )
        }
    }
}
