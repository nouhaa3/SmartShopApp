package com.example.smartshopapp.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopapp.data.model.UserProfile
import com.example.smartshopapp.data.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repo: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<UserProfile?>(null)
    val user: StateFlow<UserProfile?> = _user

    fun loadUser(uid: String) {
        viewModelScope.launch {
            _user.value = repo.getUser(uid)
        }
    }
}


