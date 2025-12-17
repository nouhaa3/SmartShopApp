package com.example.smartshopapp.data.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ImageStorageService {

    private val storage = FirebaseStorage.getInstance()
    private val imagesRef = storage.reference.child("product_images")

    suspend fun uploadImage(uri: Uri): String {
        val imageRef = imagesRef.child("${System.currentTimeMillis()}.jpg")
        imageRef.putFile(uri).await()
        return imageRef.downloadUrl.await().toString()
    }
}
