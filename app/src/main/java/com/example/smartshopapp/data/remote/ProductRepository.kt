package com.example.smartshopapp.data.remote

import android.content.Context
import com.example.smartshopapp.data.local.AppDatabase
import com.example.smartshopapp.data.model.Product
import com.example.smartshopapp.data.model.toEntity
import com.example.smartshopapp.data.model.toProduct
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class ProductRepository(context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val productsRef = db.collection("products")
    private val storage = FirebaseStorage.getInstance()
    private val imagesRef = storage.reference.child("product_images")

    private val localDb = AppDatabase.getInstance(context)
    private val dao = localDb.productDao()

    private val ioScope = CoroutineScope(Dispatchers.IO)

    // ------------------------------
    // MÉTHODES EXISTANTES
    // ------------------------------

    suspend fun uploadProductImage(productId: String, imageUri: Uri): String {
        val imageRef = imagesRef.child("$productId.jpg")
        imageRef.putFile(imageUri).await()
        return imageRef.downloadUrl.await().toString()
    }

    suspend fun getProducts(): List<Product> {
        return dao.getAllProducts().map { it.toProduct() }
    }

    suspend fun getAllProductsOnce(): List<Product> {
        return dao.getAllProducts().map { it.toProduct() }
    }

    suspend fun getProductById(id: String): Product? {
        return dao.getById(id)?.toProduct()
    }

    suspend fun addProduct(product: Product) {
        productsRef.document(product.id).set(product).await()
        dao.insertProduct(product.toEntity())
    }

    suspend fun updateProduct(product: Product) {
        productsRef.document(product.id).set(product).await()
        dao.insertProduct(product.toEntity())
    }

    suspend fun deleteProduct(id: String) {
        productsRef.document(id).delete().await()
        dao.deleteById(id)
    }

    suspend fun deleteProductImage(imagePath: String) {
        try {
            if (imagePath.startsWith("https://")) {
                try {
                    val imageRef = storage.getReferenceFromUrl(imagePath)
                    imageRef.delete().await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val file = File(imagePath)
                if (file.exists()) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startRealtimeSync() {
        productsRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = snapshot.toObjects(Product::class.java)
                ioScope.launch {
                    list.forEach { product ->
                        dao.insertProduct(product.toEntity())
                    }
                }
            }
        }
    }

    // NOUVELLES MÉTHODES DE FILTRAGE

    suspend fun getAvailableProducts(): List<Product> {
        return dao.getAvailableProducts().map { it.toProduct() }
    }

    suspend fun getLowStockProducts(): List<Product> {
        return dao.getLowStockProducts().map { it.toProduct() }
    }

    suspend fun getOutOfStockProducts(): List<Product> {
        return dao.getOutOfStockProducts().map { it.toProduct() }
    }

    suspend fun getProductsByCategory(category: String): List<Product> {
        return dao.getProductsByCategory(category).map { it.toProduct() }
    }

    suspend fun getAvailableProductsByCategory(category: String): List<Product> {
        return dao.getAvailableProductsByCategory(category).map { it.toProduct() }
    }
}