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

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    private val _authorized = SingleLiveEvent<AuthModelState>()
    val authorized: LiveData<AuthModelState>
        get() = _authorized

    fun signIn(login: String, pass: String) = viewModelScope.launch {
        _authorized.value = AuthModelState(authLoading = true)
        try {
            val response = repository.singIn(login, pass)
            response.token?.let { AppAuth.getInstance().setAuth(response.id, response.token) }
            _authorized.value = AuthModelState(authSuccessful = true)
        } catch (e: Exception) {
            _authorized.value = AuthModelState(authError = true)
        }
    }
}