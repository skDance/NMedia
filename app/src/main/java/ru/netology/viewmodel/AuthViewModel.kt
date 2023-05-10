package ru.netology.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.auth.AppAuth

class AuthViewModel : ViewModel() {

    val state = AppAuth.getInstance().authStateFlow.asLiveData()
    val authorized: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L
}