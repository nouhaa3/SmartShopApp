package com.example.smartshopapp.data.remote

import com.example.smartshopapp.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreProductService {

    private val db = FirebaseFirestore.getInstance()
    private val productsRef = db.collection("products")

    suspend fun getProducts(): List<Product> {
        return productsRef.get().await().toObjects(Product::class.java)
    }

    suspend fun addProduct(product: Product): Boolean {
        return try {
            val newId = productsRef.document().id
            product.id = newId
            productsRef.document(newId).set(product).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getProductsOnce(): List<Product> {
        return try {
            val snapshot = productsRef.get().await()
            snapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }


    suspend fun updateProduct(product: Product): Boolean {
        return try {
            productsRef.document(product.id).set(product).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteProduct(productId: String): Boolean {
        return try {
            productsRef.document(productId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getProductById(id: String): Product? {
        return productsRef.document(id).get().await().toObject(Product::class.java)
    }
}
