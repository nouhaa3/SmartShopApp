package com.example.smartshopapp.data.model

data class Product(
    var id: String = "",
    val name: String = "",
    val category: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val imagePath: String? = null,

    // NOUVEAUX CHAMPS
    val isAvailable: Boolean = true,      // Visible aux clients
    val lowStockThreshold: Int = 5        // Seuil d'alerte stock bas
) {
    // PROPRIÉTÉS CALCULÉES
    val isLowStock: Boolean
        get() = quantity > 0 && quantity <= lowStockThreshold

    val isOutOfStock: Boolean
        get() = quantity == 0

    val stockStatus: StockStatus
        get() = when {
            isOutOfStock -> StockStatus.OUT_OF_STOCK
            isLowStock -> StockStatus.LOW_STOCK
            else -> StockStatus.IN_STOCK
        }
}