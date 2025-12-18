package com.example.smartshopapp.ui.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.domain.ProductListViewModel
import com.example.smartshopapp.ui.theme.OldRose
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit,
    onBack: () -> Unit
) {
    val products by viewModel.products.collectAsState()

    Scaffold(
        containerColor = OldRose,
        topBar = {
            TopAppBar(
                title = { Text("My Products", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OldRose)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct,
                containerColor = Color.White,
                contentColor = OldRose
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(products) { product ->
                ProductCard(product = product) {
                    onEditProduct(product)
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        OldRose.copy(alpha = 0.15f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                product.imagePath?.let {
                    Image(
                        painter = rememberAsyncImagePainter(File(it)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text("${product.price} DT", color = OldRose)
            }
        }
    }
}
