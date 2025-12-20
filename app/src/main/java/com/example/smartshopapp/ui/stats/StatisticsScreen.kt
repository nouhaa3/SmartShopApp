package com.example.smartshopapp.ui.stats

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var totalProducts by remember { mutableStateOf(0) }
    var totalStock by remember { mutableStateOf(0) }
    var avgPrice by remember { mutableStateOf(0.0) }
    var highestPrice by remember { mutableStateOf(0.0) }
    var maxPriceProduct by remember { mutableStateOf<String?>(null) }
    var chartData by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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
                "Export Products",
                fontWeight = FontWeight.Bold,
                color = SpaceIndigo
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Choose export format:",
                    color = SpaceIndigo.copy(alpha = 0.7f)
                )

                ExportOptionButton(
                    text = "Export as CSV",
                    description = "Excel compatible format",
                    onClick = onExportCSV
                )

                ExportOptionButton(
                    text = "Export as PDF",
                    description = "Professional document format",
                    onClick = onExportPDF
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SpaceIndigo)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

@Composable
private fun ExportOptionButton(
    text: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = OldRose.copy(alpha = 0.1f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                color = SpaceIndigo,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = SpaceIndigo.copy(alpha = 0.6f)
            )
        }
    }
}

// Export Functions
private suspend fun exportToCSV(
    context: Context,
    products: List<com.example.smartshopapp.data.model.Product>
): String = withContext(Dispatchers.IO) {
    try {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "SmartShop_Products_$timestamp.csv"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Android 10+ : Utiliser MediaStore
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
                    // Header
                    outputStream.write("Name,Category,Price (DT),Quantity\n".toByteArray())

                    // Data rows
                    products.forEach { product ->
                        val row = "${escapeCsv(product.name)},${escapeCsv(product.category)}," +
                                "${product.price},${product.quantity}\n"
                        outputStream.write(row.toByteArray())
                    }
                }
                "✅ CSV saved in Downloads folder"
            } ?: "❌ Export failed"
        } else {
            // Android 9 et inférieur
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS
            )
            if (!downloadsDir.exists()) downloadsDir.mkdirs()

            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { fos ->
                // Header
                fos.write("Name,Category,Price (DT),Quantity\n".toByteArray())

                // Data rows
                products.forEach { product ->
                    val row = "${escapeCsv(product.name)},${escapeCsv(product.category)}," +
                            "${product.price},${product.quantity}\n"
                    fos.write(row.toByteArray())
                }
            }
            "✅ CSV saved: ${file.absolutePath}"
        }
    } catch (e: Exception) {
        "❌ Export failed: ${e.message}"
    }
}

private suspend fun exportToPDF(
    context: Context,
    products: List<com.example.smartshopapp.data.model.Product>
): String = withContext(Dispatchers.IO) {
    try {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "SmartShop_Products_$timestamp.pdf"

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Styling
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

        // Title
        canvas.drawText("Products List", 50f, yPos, titlePaint)
        yPos += 30f

        canvas.drawText("Generated: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}",
            50f, yPos, bodyPaint)
        yPos += 40f

        // Table Header
        canvas.drawText("Name", 50f, yPos, headerPaint)
        canvas.drawText("Category", 200f, yPos, headerPaint)
        canvas.drawText("Price", 350f, yPos, headerPaint)
        canvas.drawText("Stock", 450f, yPos, headerPaint)
        yPos += 20f

        // Draw line
        canvas.drawLine(50f, yPos, 545f, yPos, headerPaint)
        yPos += 15f

        // Data rows
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

        // Sauvegarder le PDF
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Android 10+ : Utiliser MediaStore
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
                "✅ PDF saved in Downloads folder"
            } ?: run {
                pdfDocument.close()
                "❌ Export failed"
            }
        } else {
            // Android 9 et inférieur
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS
            )
            if (!downloadsDir.exists()) downloadsDir.mkdirs()

            val file = File(downloadsDir, fileName)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            "✅ PDF saved: ${file.absolutePath}"
        }
    } catch (e: Exception) {
        "❌ Export failed: ${e.message}"
    }
}

private fun escapeCsv(value: String): String {
    return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
        "\"${value.replace("\"", "\"\"")}\""
    } else {
        value
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