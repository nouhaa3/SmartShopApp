package com.example.smartshopapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: String)

    // NOUVELLES REQUÊTES POUR FILTRAGE

    @Query("SELECT * FROM products WHERE isAvailable = 1")
    suspend fun getAvailableProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE quantity <= lowStockThreshold AND quantity > 0")
    suspend fun getLowStockProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE quantity = 0")
    suspend fun getOutOfStockProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE category = :category")
    suspend fun getProductsByCategory(category: String): List<ProductEntity>

    @Query("SELECT * FROM products WHERE category = :category AND isAvailable = 1")
    suspend fun getAvailableProductsByCategory(category: String): List<ProductEntity>
}