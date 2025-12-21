package com.example.smartshopapp.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopapp.data.model.Category
import com.example.smartshopapp.data.remote.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadCategories()
    }

    /** -------------------------------
     * LOAD CATEGORIES
    -------------------------------- */
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _categories.value = repository.getAllCategories()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** -------------------------------
     * ADD CATEGORY
    -------------------------------- */
    fun addCategory(
        name: String,
        icon: String = "📦",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank()) {
            onError("Category name cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                val category = Category(
                    id = System.currentTimeMillis().toString(),
                    name = name,
                    order = (_categories.value.maxOfOrNull { it.order } ?: 0) + 1,
                    isActive = true
                )

                repository.addCategory(category)
                loadCategories()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error adding category")
            }
        }
    }

    /** -------------------------------
     * UPDATE CATEGORY
    -------------------------------- */
    fun updateCategory(
        category: Category,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (category.name.isBlank()) {
            onError("Category name cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                repository.updateCategory(category)
                loadCategories()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error updating category")
            }
        }
    }

    /** -------------------------------
     * DELETE CATEGORY
    -------------------------------- */
    fun deleteCategory(
        categoryId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(categoryId)
                loadCategories()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error deleting category")
            }
        }
    }

    /** -------------------------------
     * TOGGLE CATEGORY ACTIVE STATUS
    -------------------------------- */
    fun toggleCategoryStatus(category: Category) {
        viewModelScope.launch {
            try {
                val updated = category.copy(isActive = !category.isActive)
                repository.updateCategory(updated)
                loadCategories()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** -------------------------------
     * REORDER CATEGORIES
    -------------------------------- */
    fun reorderCategories(
        from: Int,
        to: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val mutableList = _categories.value.toMutableList()
                val item = mutableList.removeAt(from)
                mutableList.add(to, item)

                // Update order for all categories
                mutableList.forEachIndexed { index, category ->
                    repository.updateCategory(category.copy(order = index))
                }

                loadCategories()
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** -------------------------------
     * INITIALIZE DEFAULT CATEGORIES
    -------------------------------- */
    fun initializeDefaults() {
        viewModelScope.launch {
            repository.initializeDefaultCategories()
            loadCategories()
        }
    }
}