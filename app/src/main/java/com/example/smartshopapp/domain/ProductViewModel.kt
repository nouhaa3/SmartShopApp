package com.example.smartshopapp.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.data.remote.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repo: ProductRepository
) : ViewModel() {

    fun addProduct(
        name: String,
        quantity: Int,
        price: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val product = Product(
                id = "",
                name = name,
                quantity = quantity,
                price = price
            )

            val result = repo.addProduct(product)
            if (result) onSuccess()
        }
    }

    fun updateProduct(
        product: Product,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repo.updateProduct(product)
            if (result) onSuccess()
        }
    }

    suspend fun getProduct(id: String): Product? {
        return repo.getProductById(id)
    }
}
