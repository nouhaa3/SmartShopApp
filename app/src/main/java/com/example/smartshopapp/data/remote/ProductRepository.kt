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

    // ROOM LOCAL DB
    private val localDb = AppDatabase.getInstance(context)
    private val dao = localDb.productDao()

    private val ioScope = CoroutineScope(Dispatchers.IO)

    // ------------------------------
    // UPLOAD IMAGE
    // ------------------------------
    suspend fun uploadProductImage(
        productId: String,
        imageUri: Uri
    ): String {
        val imageRef = imagesRef.child("$productId.jpg")
        imageRef.putFile(imageUri).await()
        return imageRef.downloadUrl.await().toString()
    }

    // ------------------------------
    // GET PRODUCTS (LOCAL FIRST)
    // ------------------------------
    suspend fun getProducts(): List<Product> {
        return dao.getAllProducts().map { it.toProduct() }
    }

    // ------------------------------
    // FONCTIONS POUR STATS
    // ------------------------------
    suspend fun getAllProductsOnce(): List<Product> {
        return dao.getAllProducts().map { it.toProduct() }
    }

    suspend fun getProductById(id: String): Product? {
        return dao.getById(id)?.toProduct()
    }


    // ------------------------------
    // ADD PRODUCT
    // ------------------------------
    suspend fun addProduct(product: Product) {
        productsRef.document(product.id).set(product).await()
        dao.insertProduct(product.toEntity())
    }

    // ------------------------------
    // UPDATE PRODUCT
    // ------------------------------
    suspend fun updateProduct(product: Product) {
        productsRef.document(product.id).set(product).await()
        dao.insertProduct(product.toEntity())
    }

    // ------------------------------
    // DELETE PRODUCT
    // ------------------------------
    suspend fun deleteProduct(id: String) {
        productsRef.document(id).delete().await()
        dao.deleteById(id)
    }

    // ------------------------------
    // DELETE PRODUCT IMAGE
    // ------------------------------
    suspend fun deleteProductImage(imagePath: String) {
        try {
            // Si c'est une URL Firebase Storage
            if (imagePath.startsWith("https://")) {
                try {
                    val imageRef = storage.getReferenceFromUrl(imagePath)
                    imageRef.delete().await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            // Si c'est un fichier local
            else {
                val file = File(imagePath)
                if (file.exists()) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ------------------------------
    // FIRESTORE → LOCAL SYNC
    // ------------------------------
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
}
