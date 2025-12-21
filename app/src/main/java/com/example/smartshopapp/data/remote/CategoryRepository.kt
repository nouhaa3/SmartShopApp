package com.example.smartshopapp.data.remote

import android.content.Context
import com.example.smartshopapp.data.local.AppDatabase
import com.example.smartshopapp.data.model.Category
import com.example.smartshopapp.data.model.toCategory
import com.example.smartshopapp.data.model.toEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CategoryRepository(context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val categoriesRef = db.collection("categories")

    private val localDb = AppDatabase.getInstance(context)
    private val dao = localDb.categoryDao()
    private val ioScope = CoroutineScope(Dispatchers.IO)

    // ------------------------------
    // GET CATEGORIES
    // ------------------------------
    suspend fun getAllCategories(): List<Category> {
        return dao.getAllCategories().map { it.toCategory() }
    }

    suspend fun getActiveCategories(): List<Category> {
        return dao.getAllActiveCategories().map { it.toCategory() }
    }

    fun getAllCategoriesFlow(): Flow<List<Category>> {
        return dao.getAllCategoriesFlow().map { list ->
            list.map { it.toCategory() }
        }
    }

    // ------------------------------
    // ADD CATEGORY
    // ------------------------------
    suspend fun addCategory(category: Category) {
        try {
            categoriesRef.document(category.id).set(category).await()
            dao.insertCategory(category.toEntity())
        } catch (e: Exception) {
            // At minimum, save locally if Firebase fails
            dao.insertCategory(category.toEntity())
            throw e // Re-throw to let ViewModel handle it
        }
    }

    // ------------------------------
    // UPDATE CATEGORY
    // ------------------------------
    suspend fun updateCategory(category: Category) {
        categoriesRef.document(category.id).set(category).await()
        dao.insertCategory(category.toEntity())
    }

    // ------------------------------
    // DELETE CATEGORY
    // ------------------------------
    suspend fun deleteCategory(id: String) {
        categoriesRef.document(id).delete().await()
        dao.deleteById(id)
    }

    // ------------------------------
    // INITIALIZE DEFAULT CATEGORIES
    // ------------------------------
    suspend fun initializeDefaultCategories() {
        val existing = dao.getAllCategories()
        if (existing.isEmpty()) {
            val defaultCategories = listOf(
                Category("cat_1", "Rings", 1, true),
                Category("cat_2", "Necklaces",  2, true),
                Category("cat_3", "Bracelets", 3, true),
                Category("cat_4", "Earrings", 4, true),
                Category("cat_5", "Watches", 5, true)
            )

            defaultCategories.forEach { category ->
                addCategory(category)
            }
        }
    }

    // ------------------------------
    // FIRESTORE → LOCAL SYNC
    // ------------------------------
    fun startRealtimeSync() {
        categoriesRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = snapshot.toObjects(Category::class.java)
                ioScope.launch {
                    list.forEach { category ->
                        dao.insertCategory(category.toEntity())
                    }
                }
            }
        }
    }
}