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
    val snackbarHostState = remember { SnackbarHostState() }

    var product by remember { mutableStateOf<Product?>(null) }
    var name by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId) {
        val loaded = viewModel.getProductById(productId)
        product = loaded
        loaded?.let {
            name = it.name
            quantityText = it.quantity.toString()
            priceText = it.price.toString()
        }
    }

    fun validateUI(): Boolean {
        var ok = true

        nameError = if (name.isBlank()) { ok = false; "Nom requis" } else null

        val q = quantityText.toIntOrNull()
        quantityError = when {
            quantityText.isBlank() -> { ok = false; "Quantité requise" }
            q == null -> { ok = false; "Quantité invalide" }
            q < 0 -> { ok = false; "Quantité >= 0" }
            else -> null
        }

        val p = priceText.toDoubleOrNull()
        priceError = when {
            priceText.isBlank() -> { ok = false; "Prix requis" }
            p == null -> { ok = false; "Prix invalide" }
            p <= 0.0 -> { ok = false; "Prix > 0" }
            else -> null
        }

        return ok
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Product") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
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
                onValueChange = {
                    name = it
                    nameError = null
                },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null
            )
            if (nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = quantityText,
                onValueChange = {
                    quantityText = it.filter { c -> c.isDigit() }
                    quantityError = null
                },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                isError = quantityError != null
            )
            if (quantityError != null) Text(quantityError!!, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = priceText,
                onValueChange = {
                    priceText = it.filter { c -> c.isDigit() || c == '.' || c == ',' }
                        .replace(',', '.')
                    priceError = null
                },
                label = { Text("Price (DT)") },
                modifier = Modifier.fillMaxWidth(),
                isError = priceError != null
            )
            if (priceError != null) Text(priceError!!, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (!validateUI()) return@Button

                    val updated = product!!.copy(
                        name = name,
                        quantity = quantityText.toInt(),
                        price = priceText.toDouble()
                    )

                    viewModel.updateProduct(
                        updated,
                        onSuccess = { onBack() },
                        onError = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Product")
            }
        }
    }
}
