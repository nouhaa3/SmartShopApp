package com.example.smartshopapp.data.remote

import com.example.smartshopapp.data.model.Product

class ProductRepository(
    private val firestore: FirestoreProductService = FirestoreProductService()
) {

    suspend fun getProducts(): List<Product> {
        return firestore.getProducts()
    }

    suspend fun addProduct(product: Product): Boolean {
        return firestore.addProduct(product)
    }

    suspend fun updateProduct(product: Product): Boolean {
        return firestore.updateProduct(product)
    }

    suspend fun getAllProductsOnce(): List<Product> {
        return firestore.getProductsOnce()
    }

    suspend fun deleteProduct(id: String): Boolean {
        return firestore.deleteProduct(id)
    }

    suspend fun getProductById(id: String): Product? {
        return firestore.getProductById(id)
    }
}
