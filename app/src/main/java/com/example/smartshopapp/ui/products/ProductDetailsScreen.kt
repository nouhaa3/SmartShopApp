package com.example.smartshopapp.ui.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.io.File
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.ui.theme.OldRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Scaffold(
        containerColor = OldRose,
        topBar = {
            TopAppBar(
                title = {
                    Text(product.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                },
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
                .background(OldRose)
        ) {

            // IMAGE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                if (product.imagePath != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = File(product.imagePath)
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image", color = Color.White)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, OldRose.copy(alpha = 0.6f))
                            )
                        )
                )
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoRow("Category", product.category)
                    InfoRow("Quantity", product.quantity.toString())
                    InfoRow("Price", "${product.price} DT", highlight = true)
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Icon(Icons.Default.Edit, null, tint = OldRose)
                    Spacer(Modifier.width(8.dp))
                    Text("Edit", color = OldRose)
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Icon(Icons.Default.Delete, null, tint = OldRose)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete", color = OldRose)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(
            value,
            color = if (highlight) OldRose else Color.Black,
            fontWeight = FontWeight.SemiBold
        )
    }
}
