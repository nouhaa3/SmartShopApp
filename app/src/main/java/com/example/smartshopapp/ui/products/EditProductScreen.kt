package com.example.smartshopapp.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartshopapp.domain.ProductViewModel
import com.example.smartshopapp.data.local.ProductEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    viewModel: ProductViewModel,
    productId: Int,
    onBack: () -> Unit
) {
    val state = viewModel.uiState.collectAsState().value
    val product = state.products.find { it.id == productId }

    // If product not found → return to list
    if (product == null) {
        onBack()
        return
    }

    var name by remember { mutableStateOf(product.name) }
    var quantity by remember { mutableStateOf(product.quantity.toString()) }
    var price by remember { mutableStateOf(product.price.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Product") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price DT") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val updatedProduct = ProductEntity(
                        id = productId,
                        name = name,
                        quantity = quantity.toIntOrNull() ?: 0,
                        price = price.toDoubleOrNull() ?: 0.0
                    )
                    viewModel.updateProduct(updatedProduct)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }

            if (state.loading) {
                CircularProgressIndicator()
            }

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
