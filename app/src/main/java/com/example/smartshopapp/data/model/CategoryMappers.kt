package com.example.smartshopapp.data.model

import com.example.smartshopapp.data.local.CategoryEntity

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    order = order,
    isActive = isActive
)

fun CategoryEntity.toCategory() = Category(
    id = id,
    name = name,
    order = order,
    isActive = isActive
)