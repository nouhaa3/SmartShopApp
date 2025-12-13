package com.example.smartshopapp.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.data.remote.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repo: ProductRepository
) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    suspend fun getProductById(id: String): Product? {
        return repo.getProductById(id)
    }

    // ----------------------------
    // ADD PRODUCT
    // ----------------------------
    fun addProduct(
        name: String,
        quantity: Int,
        price: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val validationError = validateProductFields(name, quantity, price)
        if (validationError != null) {
            onError(validationError)
            return
        }

        viewModelScope.launch {
            try {
                val product = Product(
                    id = System.currentTimeMillis().toString(), // simple unique ID
                    name = name,
                    quantity = quantity,
                    price = price
                )

                repo.addProduct(product) // returns Unit → no check needed

                onSuccess()

            } catch (e: Exception) {
                onError(e.message ?: "Erreur inconnue lors de l'ajout.")
            }
        }
    }

    // ----------------------------
    // UPDATE PRODUCT
    // ----------------------------
    fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val validationError = validateProductFields(product.name, product.quantity, product.price)
        if (validationError != null) {
            onError(validationError)
            return
        }

        viewModelScope.launch {
            try {
                repo.updateProduct(product)  // returns Unit
                onSuccess()

            } catch (e: Exception) {
                onError(e.message ?: "Erreur inconnue lors de la mise à jour.")
            }
        }
    }

    // ----------------------------
    // LOAD SINGLE PRODUCT
    // ----------------------------
    fun loadProduct(id: String) {
        viewModelScope.launch {
            val all = repo.getProducts()
            _product.value = all.find { it.id == id }
        }
    }

    // ----------------------------
    // VALIDATION
    // ----------------------------
    private fun validateProductFields(name: String, quantity: Int, price: Double): String? {
        if (name.isBlank()) return "Le nom du produit ne peut pas être vide."
        if (quantity < 0) return "La quantité doit être ≥ 0."
        if (price <= 0) return "Le prix doit être > 0."
        return null
    }
}
