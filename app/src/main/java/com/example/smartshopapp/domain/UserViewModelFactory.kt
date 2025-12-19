package com.example.smartshopapp.domain

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.smartshopapp.data.remote.UserRepository
import androidx.lifecycle.ViewModel

class UserViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(UserRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
