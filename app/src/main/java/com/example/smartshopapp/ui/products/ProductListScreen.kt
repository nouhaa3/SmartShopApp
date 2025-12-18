package com.example.smartshopapp.ui.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.smartshopapp.ui.theme.SpaceIndigo
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

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var filterExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "All",
        "Rings",
        "Necklaces",
        "Bracelets",
        "Earrings",
        "Watches"
    )

    val filteredProducts = products.filter { product ->
        val matchSearch =
            product.name.contains(searchQuery, ignoreCase = true)

        val matchCategory =
            selectedCategory == "All" || product.category == selectedCategory

        matchSearch && matchCategory
    }

    Scaffold(
        containerColor = OldRose,
        topBar = {
            TopAppBar(
                title = {
                    Text("My Products", color = Color.White)
                },
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

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            /* ---------- SEARCH + FILTER ---------- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                Spacer(Modifier.width(8.dp))

                Box {
                    IconButton(
                        onClick = { filterExpanded = true },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                    ) {
                        Icon(Icons.Default.FilterList, null, tint = OldRose)
                    }

                    DropdownMenu(
                        expanded = filterExpanded,
                        onDismissRequest = { filterExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    filterExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- GRID ---------- */
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No products found",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredProducts) { product ->
                        ProductGridCard(
                            product = product,
                            onClick = { onEditProduct(product) }
                        )
                    }
                }
            }
        }
    }
}

/* ---------- GRID CARD ---------- */

@Composable
private fun ProductGridCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {

            // IMAGE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        OldRose.copy(alpha = 0.15f),
                        RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
                    ),
                contentAlignment = Alignment.Center
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

            Spacer(Modifier.height(8.dp))

            // TEXT
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    color = SpaceIndigo,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${product.price} DT",
                    color = OldRose,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
