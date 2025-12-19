package com.example.smartshopapp.data.remote

import android.content.Context
import com.example.smartshopapp.data.local.AppDatabase
import com.example.smartshopapp.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersRef = firestore.collection("users")

    private val dao = AppDatabase.getInstance(context).userDao()

    suspend fun getUser(uid: String): UserProfile? {
        return dao.getUser(uid)?.toUserProfile()
    }

    suspend fun saveUser(user: UserProfile) {
        usersRef.document(user.uid).set(user).await()
        dao.insertUser(user.toEntity())
    }

    suspend fun syncUser(uid: String) {
        val snap = usersRef.document(uid).get().await()
        snap.toObject(UserProfile::class.java)?.let {
            dao.insertUser(it.toEntity())
        }
    }
}
