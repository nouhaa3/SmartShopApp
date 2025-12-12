package com.example.smartshopapp.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun BarChart(data: List<Pair<String, Int>>) {

    if (data.isEmpty()) {
        Text("No data to display.")
        return
    }

    val maxValue = data.maxOf { it.second }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(10.dp)
    ) {
        val barWidth = size.width / (data.size * 2)

        data.forEachIndexed { index, pair ->
            val barHeight = (pair.second / maxValue.toFloat()) * size.height

            drawRect(
                color = Color(0xFF4CAF50),
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = index * barWidth * 2f,
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
