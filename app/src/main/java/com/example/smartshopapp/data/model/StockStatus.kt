package com.example.smartshopapp.data.model

enum class StockStatus(val label: String, val color: Long) {
    IN_STOCK("In Stock", 0xFF4CAF50),      // Vert
    LOW_STOCK("Low Stock", 0xFFFF9800),    // Orange
    OUT_OF_STOCK("Out of Stock", 0xFFF44336) // Rouge
}