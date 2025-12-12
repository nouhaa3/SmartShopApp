package com.example.smartshopapp.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.domain.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String,
    viewModel: ProductViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var product by remember { mutableStateOf<Product?>(null) }
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    // Load product once
    LaunchedEffect(true) {
        val loaded = viewModel.getProduct(productId)
        product = loaded

        loaded?.let {
            name = it.name
            quantity = it.quantity.toString()
            price = it.price.toString()
        }
    }

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
                .fillMaxWidth()
        ) {

            if (product == null) {
                Text("Loading...")
                return@Column
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        val updated = product!!.copy(
                            name = name,
                            quantity = quantity.toIntOrNull() ?: 0,
                            price = price.toDoubleOrNull() ?: 0.0
                        )
                        viewModel.updateProduct(updated) {
                            onBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Product")
            }
        }
    }
}
