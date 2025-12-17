package com.example.smartshopapp.data.remote

import android.net.Uri
import com.example.smartshopapp.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirestoreProductService {

    private val firestore = FirebaseFirestore.getInstance()
    private val productsRef = firestore.collection("products")

    private val storage = FirebaseStorage.getInstance()
    private val imagesRef = storage.reference.child("product_images")

    // ---------------- IMAGE UPLOAD ----------------
    suspend fun uploadProductImage(uri: Uri): String {
        val imageRef = imagesRef.child("${System.currentTimeMillis()}.jpg")
        imageRef.putFile(uri).await()
        return imageRef.downloadUrl.await().toString()
    }

    // ---------------- ADD PRODUCT ----------------
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
        return productsRef.get().await().toObjects(Product::class.java)
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
