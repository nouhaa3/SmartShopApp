package com.example.smartshopapp.ui.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun copyImageToInternalStorage(context: Context, uri: Uri): String {
    val imagesDir = File(context.filesDir, "images")
    if (!imagesDir.exists()) imagesDir.mkdir()

    val file = File(imagesDir, "img_${System.currentTimeMillis()}.jpg")

    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }

    return file.absolutePath // STRING PATH
}
