package com.example.smartshopapp.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartshopapp.ui.theme.OldRose

/* -------------------- STAT CARD -------------------- */

@Composable
fun StatCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.92f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OldRose
            )
        }
    }
}

/* -------------------- BAR CHART -------------------- */

@Composable
fun BarChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Text(
            text = "No data available",
            color = Color.Gray
        )
        return
    }

    val maxValue = data.maxOf { it.second }.coerceAtLeast(1)

    Canvas(
        modifier = modifier
    ) {
        val barCount = data.size
        val spacing = 16.dp.toPx()
        val barWidth = (size.width - spacing * (barCount + 1)) / barCount

        data.forEachIndexed { index, (_, value) ->
            val barHeight = (value / maxValue.toFloat()) * size.height

            val x = spacing + index * (barWidth + spacing)
            val y = size.height - barHeight

            drawRect(
                color = OldRose,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
}
