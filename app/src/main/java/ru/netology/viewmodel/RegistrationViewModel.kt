package ru.netology.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.auth.AppAuth
import ru.netology.db.AppDb
import ru.netology.model.AuthModelState
import ru.netology.repository.PostRepository
import ru.netology.repository.PostRepositoryImpl
import ru.netology.util.SingleLiveEvent

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    private val _registered = SingleLiveEvent<AuthModelState>()
    val registered: LiveData<AuthModelState>
        get() = _registered

    fun registration(login: String, pass: String, name: String) = viewModelScope.launch {
        _registered.value = AuthModelState(authLoading = true)
        try {
            val response = repository.register(login, pass, name)
            response.token?.let { AppAuth.getInstance().setAuth(response.id, response.token) }
            _registered.value = AuthModelState(authSuccessful = true)
        } catch (e: Exception) {
            _registered.value = AuthModelState(authError = true)
        }
    }
}