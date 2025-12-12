package com.example.smartshopapp.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.data.remote.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val firestore = FirebaseFirestore.getInstance()
    private val productsRef = firestore.collection("products")
    private var listenerRegistration: ListenerRegistration? = null

    init {
        observeProducts()
    }

    /** -------------------------------
     * 🔥 REAL-TIME LISTENER
     * Automatically updates product list on:
     * - Add
     * - Edit
     * - Delete
    -------------------------------- */
    private fun observeProducts() {
        listenerRegistration = productsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                _products.value = snapshot.toObjects(Product::class.java)
            }
        }
    }

    /** -------------------------------
     * 🔥 DELETE PRODUCT
     * After deleting, Firestore listener updates the list automatically
    -------------------------------- */
    fun deleteProduct(id: String) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
