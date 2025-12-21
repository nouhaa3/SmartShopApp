package com.example.smartshopapp.data.model

import com.example.smartshopapp.data.local.ProductEntity

fun Product.toEntity() = ProductEntity(
    id = id,
    name = name,
    category = category,
    quantity = quantity,
    price = price,
    imageUri = imagePath,
    isAvailable = isAvailable,
    lowStockThreshold = lowStockThreshold
)

fun ProductEntity.toProduct() = Product(
    id = id,
    name = name,
    category = category,
    quantity = quantity,
    price = price,
    imagePath = imageUri,
    isAvailable = isAvailable,
    lowStockThreshold = lowStockThreshold
)