package com.example.smartshopapp.data.model

import com.example.smartshopapp.data.local.ProductEntity

fun ProductEntity.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        quantity = quantity,
        price = price
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        quantity = quantity,
        price = price
    )
}
