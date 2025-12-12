package com.example.smartshopapp.data.remote

import com.example.smartshopapp.data.local.ProductEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreProductService {

    private val productsRef = FirebaseFirestore.getInstance().collection("products")

    suspend fun uploadProduct(product: ProductEntity) {
        productsRef.document(product.id.toString()).set(product).await()
    }

    suspend fun deleteProduct(id: Int) {
        productsRef.document(id.toString()).delete().await()
    }

    suspend fun getAllProducts(): List<ProductEntity> {
        return productsRef.get().await().toObjects(ProductEntity::class.java)
    }
}
