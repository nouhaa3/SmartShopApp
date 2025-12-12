package com.example.smartshopapp.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartshopapp.domain.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Helper to validate UI fields; returns true if OK
    fun validateUI(): Boolean {
        var ok = true

        nameError = if (name.isBlank()) { ok = false; "Nom requis" } else null

        val q = quantityText.toIntOrNull()
        quantityError = when {
            quantityText.isBlank() -> { ok = false; "Quantité requise" }
            q == null -> { ok = false; "Quantité invalide" }
            q < 0 -> { ok = false; "Quantité >= 0 requise" }
            else -> null
        }

        val p = priceText.toDoubleOrNull()
        priceError = when {
            priceText.isBlank() -> { ok = false; "Prix requis" }
            p == null -> { ok = false; "Prix invalide" }
            p <= 0.0 -> { ok = false; "Prix > 0 requis" }
            else -> null
        }

        return ok
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Product") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxWidth()
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (nameError != null) nameError = null
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
                    quantityText = it.filter { ch -> ch.isDigit() } // allow digits only
                    if (quantityError != null) quantityError = null
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
                    // allow digits and decimal separator
                    priceText = it.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' }.replace(',', '.')
                    if (priceError != null) priceError = null
                },
                label = { Text("Price (DT)") },
                modifier = Modifier.fillMaxWidth(),
                isError = priceError != null
            )
            if (priceError != null) Text(priceError!!, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(20.dp))

            val saveEnabled = name.isNotBlank() && quantityText.isNotBlank() && priceText.isNotBlank()

            Button(
                onClick = {
                    if (!validateUI()) return@Button

                    val q = quantityText.toInt()
                    val p = priceText.toDouble()

                    // ViewModel-level validation + network call
                    viewModel.addProduct(name, q, p,
                        onSuccess = { onBack() },
                        onError = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = saveEnabled
            ) {
                Text("Save Product")
            }
        }
    }
}
