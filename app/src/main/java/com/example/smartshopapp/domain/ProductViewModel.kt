package com.example.smartshopapp.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.data.remote.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.net.Uri


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
        category: String,
        quantity: Int,
        price: Double,
        imagePath: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val product = Product(
                    id = System.currentTimeMillis().toString(),
                    name = name,
                    category = category,
                    quantity = quantity,
                    price = price,
                    imagePath = imagePath
                )

                repo.addProduct(product)
                onSuccess()

            } catch (e: Exception) {
                onError(e.message ?: "Error adding product")
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
                onError(e.message ?: "Error occurred when updating the product.")
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
        if (name.isBlank()) return "Product name can not be empty."
        if (quantity < 0) return "Quantity should be ≥ 0."
        if (price <= 0) return "Price should be > 0."
        return null
    }
}
