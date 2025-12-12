package com.example.smartshopapp.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopapp.data.local.ProductEntity
import com.example.smartshopapp.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductUiState(
    val loading: Boolean = false,
    val products: List<ProductEntity> = emptyList(),
    val error: String? = null,
    val success: Boolean = false
)

class ProductViewModel(
    private val repo: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    // -------------------------
    // LOAD PRODUCTS
    // -------------------------
    private fun loadProducts() {
        viewModelScope.launch {
            repo.getAllProducts().collect { list ->
                _uiState.value = ProductUiState(products = list)
            }
        }
    }

    // -------------------------
    // ADD PRODUCT
    // -------------------------
    fun addProduct(name: String, quantity: Int, price: Double) {
        viewModelScope.launch {
            _uiState.value = ProductUiState(loading = true)

            if (name.isBlank()) {
                _uiState.value = ProductUiState(error = "Product name cannot be empty")
                return@launch
            }

            if (price <= 0) {
                _uiState.value = ProductUiState(error = "Price must be > 0")
                return@launch
            }

            if (quantity < 0) {
                _uiState.value = ProductUiState(error = "Quantity cannot be negative")
                return@launch
            }

            try {
                repo.insertProduct(
                    ProductEntity(
                        name = name,
                        quantity = quantity,
                        price = price
                    )
                )

                _uiState.value = ProductUiState(success = true)

            } catch (e: Exception) {
                _uiState.value = ProductUiState(error = e.message)
            }
        }
    }

    // -------------------------
    // UPDATE PRODUCT
    // -------------------------
    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                repo.updateProduct(product)
                _uiState.value = ProductUiState(success = true)
            } catch (e: Exception) {
                _uiState.value = ProductUiState(error = e.message)
            }
        }
    }

    // -------------------------
    // DELETE PRODUCT
    // -------------------------
    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                repo.deleteProduct(product)
                _uiState.value = ProductUiState(success = true)
            } catch (e: Exception) {
                _uiState.value = ProductUiState(error = e.message)
            }
        }
    }

    fun resetState() {
        _uiState.value = ProductUiState()
    }
}
