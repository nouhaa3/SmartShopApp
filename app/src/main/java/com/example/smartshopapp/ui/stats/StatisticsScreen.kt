package com.example.smartshopapp.ui.stats

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.data.remote.ProductRepository
import com.example.smartshopapp.ui.theme.OldRose
import com.example.smartshopapp.ui.theme.SpaceIndigo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    repository: ProductRepository,
    onBack: () -> Unit
) {
    var totalProducts by remember { mutableIntStateOf(0) }
    var totalStock by remember { mutableIntStateOf(0) }
    var totalStockValue by remember { mutableDoubleStateOf(0.0) }
    var avgPrice by remember { mutableDoubleStateOf(0.0) }
    var highestPrice by remember { mutableDoubleStateOf(0.0) }
    var lowestPrice by remember { mutableDoubleStateOf(0.0) }
    var maxPriceProduct by remember { mutableStateOf<String?>(null) }
    var lowStockProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var topProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var categoryDistribution by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var stockByCategory by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    var showExportDialog by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // LOAD DATA
    LaunchedEffect(Unit) {
        val products = repository.getAllProductsOnce()

        totalProducts = products.size
        totalStock = products.sumOf { it.quantity }
        totalStockValue = products.sumOf { it.price * it.quantity }
        avgPrice = if (products.isNotEmpty()) {
            products.map { it.price }.average()
        } else 0.0

        highestPrice = products.maxOfOrNull { it.price } ?: 0.0
        lowestPrice = products.minOfOrNull { it.price } ?: 0.0
        maxPriceProduct = products.maxByOrNull { it.price }?.name

        // Low stock products (quantity < 5)
        lowStockProducts = products.filter { it.quantity < 5 }.sortedBy { it.quantity }

        // Top products by stock value
        topProducts = products.sortedByDescending { it.price * it.quantity }.take(5)

        // Category distribution
        categoryDistribution = products.groupBy { it.category }
            .mapValues { it.value.size }

        // Stock by category for chart
        stockByCategory = products.groupBy { it.category }
            .map { (category, prods) -> category to prods.sumOf { it.quantity } }
            .sortedByDescending { it.second }
    }

    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            snackbarHostState.showSnackbar(
                message = exportMessage,
                duration = SnackbarDuration.Short
            )
            showSuccessSnackbar = false
        }
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
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = SpaceIndigo,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Analytics Dashboard",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            letterSpacing = 0.5.sp
                        )
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
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    },
                    actions = {
                        Surface(
                            onClick = { showExportDialog = true },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(44.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.FileDownload,
                                    contentDescription = "Export",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(top = 8.dp)
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

                /* ---------- OVERVIEW STATS ---------- */
                Text(
                    "Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.3.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCard(
                        title = "Total Products",
                        value = totalProducts.toString(),
                        subtitle = "In inventory",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Stock",
                        value = totalStock.toString(),
                        subtitle = "Items available",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCard(
                        title = "Stock Value",
                        value = "%.0f DT".format(totalStockValue),
                        subtitle = "Total worth",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Avg Price",
                        value = "%.2f DT".format(avgPrice),
                        subtitle = "Per product",
                        modifier = Modifier.weight(1f)
                    )
                }

                /* ---------- PRICE RANGE ---------- */
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Price Range",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpaceIndigo,
                            letterSpacing = 0.3.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Lowest",
                                    fontSize = 12.sp,
                                    color = SpaceIndigo.copy(alpha = 0.6f)
                                )
                                Text(
                                    "%.2f DT".format(lowestPrice),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OldRose
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Highest",
                                    fontSize = 12.sp,
                                    color = SpaceIndigo.copy(alpha = 0.6f)
                                )
                                Text(
                                    "%.2f DT".format(highestPrice),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OldRose
                                )
                            }
                        }

                        maxPriceProduct?.let {
                            HorizontalDivider(
                                color = SpaceIndigo.copy(alpha = 0.1f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = OldRose,
                                    modifier = Modifier.size(16.dp)
                                )
                                Column {
                                    Text(
                                        "Most Expensive",
                                        fontSize = 11.sp,
                                        color = SpaceIndigo.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        it,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SpaceIndigo
                                    )
                                }
                            }
                        }
                    }
                }

                /* ---------- CATEGORY DISTRIBUTION ---------- */
                Text(
                    "Category Analysis",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.3.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Products by Category",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpaceIndigo
                        )

                        if (categoryDistribution.isNotEmpty()) {
                            categoryDistribution.forEach { (category, count) ->
                                CategoryRow(
                                    category = category,
                                    count = count,
                                    total = totalProducts
                                )
                            }
                        } else {
                            Text(
                                "No data available",
                                fontSize = 14.sp,
                                color = SpaceIndigo.copy(alpha = 0.5f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                /* ---------- STOCK BY CATEGORY CHART ---------- */
                if (stockByCategory.isNotEmpty()) {
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
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpaceIndigo,
                                letterSpacing = 0.3.sp
                            )
                            Spacer(Modifier.height(16.dp))
                            ImprovedBarChart(
                                data = stockByCategory,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                            )
                        }
                    }
                }

                /* ---------- TOP PRODUCTS ---------- */
                if (topProducts.isNotEmpty()) {
                    Text(
                        "Top Products by Value",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.3.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            topProducts.forEach { product ->
                                TopProductRow(product = product)
                                if (product != topProducts.last()) {
                                    HorizontalDivider(
                                        color = SpaceIndigo.copy(alpha = 0.1f)
                                    )
                                }
                            }
                        }
                    }
                }

                /* ---------- LOW STOCK ALERT ---------- */
                if (lowStockProducts.isNotEmpty()) {
                    Text(
                        "Low Stock Alert",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.3.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFFFF3E0),
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Products running low (< 5 items)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFE65100)
                                )
                            }

                            lowStockProducts.forEach { product ->
                                LowStockRow(product = product)
                                if (product != lowStockProducts.last()) {
                                    HorizontalDivider(
                                        color = Color(0xFFFF9800).copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }

        // Export Dialog
        if (showExportDialog) {
            ExportDialog(
                onDismiss = { showExportDialog = false },
                onExportCSV = {
                    scope.launch {
                        val products = repository.getAllProductsOnce()
                        val result = exportToCSV(context, products)
                        exportMessage = result
                        showSuccessSnackbar = true
                        showExportDialog = false
                    }
                },
                onExportPDF = {
                    scope.launch {
                        val products = repository.getAllProductsOnce()
                        val result = exportToPDF(context, products)
                        exportMessage = result
                        showSuccessSnackbar = true
                        showExportDialog = false
                    }
                }
            )
        }
    }
}

/* ---------- STAT CARD ---------- */
@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                fontSize = 12.sp,
                color = SpaceIndigo.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp
            )
            Column {
                Text(
                    value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OldRose,
                    letterSpacing = 0.3.sp
                )
                Text(
                    subtitle,
                    fontSize = 11.sp,
                    color = SpaceIndigo.copy(alpha = 0.5f),
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

/* ---------- CATEGORY ROW ---------- */
@Composable
private fun CategoryRow(
    category: String,
    count: Int,
    total: Int
) {
    val percentage = if (total > 0) (count.toFloat() / total * 100) else 0f

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                category,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SpaceIndigo
            )
            Text(
                "$count products",
                fontSize = 12.sp,
                color = SpaceIndigo.copy(alpha = 0.6f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    SpaceIndigo.copy(alpha = 0.1f),
                    RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100)
                    .fillMaxHeight()
                    .background(
                        OldRose,
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

/* ---------- TOP PRODUCT ROW ---------- */
@Composable
private fun TopProductRow(product: Product) {
    val stockValue = product.price * product.quantity

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SpaceIndigo
            )
            Text(
                "${product.quantity} items × ${product.price} DT",
                fontSize = 11.sp,
                color = SpaceIndigo.copy(alpha = 0.6f)
            )
        }
        Text(
            "%.0f DT".format(stockValue),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = OldRose
        )
    }
}

/* ---------- LOW STOCK ROW ---------- */
@Composable
private fun LowStockRow(product: Product) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFE65100)
            )
            Text(
                product.category,
                fontSize = 11.sp,
                color = Color(0xFFE65100).copy(alpha = 0.7f)
            )
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFFF9800).copy(alpha = 0.2f)
        ) {
            Text(
                "${product.quantity} left",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

/* ---------- IMPROVED BAR CHART ---------- */
@Composable
fun ImprovedBarChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No data available",
                color = SpaceIndigo.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        }
        return
    }

    val maxValue = data.maxOf { it.second }.coerceAtLeast(1)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val barCount = data.size
            val spacing = 24.dp.toPx()
            val barWidth = ((size.width - spacing * (barCount + 1)) / barCount).coerceAtLeast(30.dp.toPx())

            data.forEachIndexed { index, (_, value) ->
                val barHeight = (value / maxValue.toFloat()) * size.height * 0.9f

                val x = spacing + index * (barWidth + spacing)
                val y = size.height - barHeight

                // Draw bar
                drawRoundRect(
                    color = OldRose,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                )

                // Draw value text on top of bar
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = SpaceIndigo.toArgb()
                        textSize = 11.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    drawText(
                        value.toString(),
                        x + barWidth / 2,
                        y - 8.dp.toPx(),
                        paint
                    )
                }
            }
        }

        // Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { (label, _) ->
                Text(
                    text = label.take(8),
                    fontSize = 10.sp,
                    color = SpaceIndigo.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

/* ---------- EXPORT DIALOG ---------- */
@Composable
private fun ExportDialog(
    onDismiss: () -> Unit,
    onExportCSV: () -> Unit,
    onExportPDF: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Export Report",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = SpaceIndigo,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Choose export format:",
                    color = SpaceIndigo.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = onExportCSV,
                    colors = ButtonDefaults.buttonColors(containerColor = OldRose),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        "Export as CSV",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                OutlinedButton(
                    onClick = onExportPDF,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OldRose),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        "Export as PDF",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Cancel",
                    color = SpaceIndigo,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp
    )
}

// Export Functions remain the same as in your original code
private suspend fun exportToCSV(
    context: Context,
    products: List<Product>
): String = withContext(Dispatchers.IO) {
    try {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "SmartShop_Products_$timestamp.csv"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val values = android.content.ContentValues().apply {
                put(android.provider.MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(android.provider.MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(android.provider.MediaStore.Downloads.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = context.contentResolver.insert(
                android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            )

            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write("Name,Category,Price (DT),Quantity\n".toByteArray())
                    products.forEach { product ->
                        val row = "${escapeCsv(product.name)},${escapeCsv(product.category)}," +
                                "${product.price},${product.quantity}\n"
                        outputStream.write(row.toByteArray())
                    }
                }
                "CSV saved in Downloads"
            } ?: "Export failed"
        } else {
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS
            )
            if (!downloadsDir.exists()) downloadsDir.mkdirs()

            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { fos ->
                fos.write("Name,Category,Price (DT),Quantity\n".toByteArray())
                products.forEach { product ->
                    val row = "${escapeCsv(product.name)},${escapeCsv(product.category)}," +
                            "${product.price},${product.quantity}\n"
                    fos.write(row.toByteArray())
                }
            }
            "CSV saved: ${file.absolutePath}"
        }
    } catch (e: Exception) {
        "Export failed: ${e.message}"
    }
}

private suspend fun exportToPDF(
    context: Context,
    products: List<Product>
): String = withContext(Dispatchers.IO) {
    try {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "SmartShop_Products_$timestamp.pdf"

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val titlePaint = Paint().apply {
            color = SpaceIndigo.toArgb()
            textSize = 24f
            isFakeBoldText = true
        }
        val headerPaint = Paint().apply {
            color = OldRose.toArgb()
            textSize = 12f
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 10f
        }

        var yPos = 50f

        canvas.drawText("Products List", 50f, yPos, titlePaint)
        yPos += 30f

        canvas.drawText("Generated: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}",
            50f, yPos, bodyPaint)
        yPos += 40f

        canvas.drawText("Name", 50f, yPos, headerPaint)
        canvas.drawText("Category", 200f, yPos, headerPaint)
        canvas.drawText("Price", 350f, yPos, headerPaint)
        canvas.drawText("Stock", 450f, yPos, headerPaint)
        yPos += 20f

        canvas.drawLine(50f, yPos, 545f, yPos, headerPaint)
        yPos += 15f

        products.forEach { product ->
            if (yPos > 800) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPos = 50f
            }

            canvas.drawText(product.name.take(20), 50f, yPos, bodyPaint)
            canvas.drawText(product.category.take(15), 200f, yPos, bodyPaint)
            canvas.drawText("${product.price} DT", 350f, yPos, bodyPaint)
            canvas.drawText(product.quantity.toString(), 450f, yPos, bodyPaint)
            yPos += 20f
        }

        pdfDocument.finishPage(page)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val values = android.content.ContentValues().apply {
                put(android.provider.MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(android.provider.MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(android.provider.MediaStore.Downloads.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = context.contentResolver.insert(
                android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            )

            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                pdfDocument.close()
                "PDF saved in Downloads"
            } ?: run {
                pdfDocument.close()
                "Export failed"
            }
        } else {
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS
            )
            if (!downloadsDir.exists()) downloadsDir.mkdirs()

            val file = File(downloadsDir, fileName)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            "PDF saved: ${file.absolutePath}"
        }
    } catch (e: Exception) {
        "Export failed: ${e.message}"
    }
}

private fun escapeCsv(value: String): String {
    return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
        "\"${value.replace("\"", "\"\"")}\""
    } else {
        value
    }
}