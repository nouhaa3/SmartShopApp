package com.example.smartshopapp.data.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProductImageService {

    private val storage = FirebaseStorage.getInstance().reference

    suspend fun uploadProductImage(imageUri: Uri): String {
        val fileName = "products/${UUID.randomUUID()}.jpg"
        val ref = storage.child(fileName)

        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }
}