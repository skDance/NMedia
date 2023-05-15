package ru.netology.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {

    val state = appAuth.authStateFlow.asLiveData()
    val authorized: Boolean
        get() = appAuth.authStateFlow.value.id != 0L
}