package com.example.smartshopapp.data

import com.example.smartshopapp.data.local.ProductDao
import com.example.smartshopapp.data.local.ProductEntity
import com.example.smartshopapp.data.remote.FirestoreProductService
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val dao: ProductDao,
    private val remote: FirestoreProductService
) {

    // --------------------------
    // LOCAL ROOM OPERATIONS
    // --------------------------
    fun getAllProducts(): Flow<List<ProductEntity>> = dao.getAllProducts()

    suspend fun insertProduct(product: ProductEntity) {
        val newId = dao.insertProduct(product).toInt()
        val productWithId = product.copy(id = newId)
        remote.uploadProduct(productWithId)   // sync cloud
    }

    suspend fun updateProduct(product: ProductEntity) {
        dao.updateProduct(product)
        remote.uploadProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        dao.deleteProduct(product)
        remote.deleteProduct(product.id)
    }

    // --------------------------
    // CLOUD → LOCAL SYNC
    // --------------------------
    suspend fun syncFromCloud() {
        val cloudList = remote.getAllProducts()
        cloudList.forEach { dao.insertProduct(it) }
    }
}
