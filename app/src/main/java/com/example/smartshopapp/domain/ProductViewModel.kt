package com.example.smartshopapp.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.data.remote.ProductRepository
import kotlinx.coroutines.launch

    class ProductViewModel(
        private val repo: ProductRepository
    ) : ViewModel() {

    // addProduct with validation and onError callback
    fun addProduct(
        name: String,
        quantity: Int,
        price: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // ViewModel-level validation (duplicate of UI checks, but safer)
        val validationError = validateProductFields(name, quantity, price)
        if (validationError != null) {
            onError(validationError)
            return
        }

        viewModelScope.launch {
            try {
                val product = Product(id = "", name = name, quantity = quantity, price = price)
                val result = repo.addProduct(product)
                if (result) onSuccess() else onError("Erreur lors de l'ajout (cloud).")
            } catch (e: Exception) {
                onError(e.message ?: "Erreur inconnue lors de l'ajout.")
            }
        }
    }

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
                val result = repo.updateProduct(product)
                if (result) onSuccess() else onError("Erreur lors de la mise à jour (cloud).")
            } catch (e: Exception) {
                onError(e.message ?: "Erreur inconnue lors de la mise à jour.")
            }
        }
    }

    private fun validateProductFields(name: String, quantity: Int, price: Double): String? {
        if (name.isBlank()) return "Le nom du produit ne peut pas être vide."
        if (quantity < 0) return "La quantité doit être supérieure ou égale à 0."
        if (price <= 0.0) return "Le prix doit être strictement supérieur à 0."
        return null
    }

    suspend fun getProduct(id: String): Product? {
        return repo.getProductById(id)
    }
}
