package ru.netology.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.auth.AppAuth
import ru.netology.model.AuthModelState
import ru.netology.repository.PostRepository
import ru.netology.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
) : ViewModel() {

    private val _authorized = SingleLiveEvent<AuthModelState>()
    val authorized: LiveData<AuthModelState>
        get() = _authorized

    fun signIn(login: String, pass: String) = viewModelScope.launch {
        _authorized.value = AuthModelState(authLoading = true)
        try {
            val response = repository.singIn(login, pass)
            response.token?.let { appAuth.setAuth(response.id, response.token) }
            _authorized.value = AuthModelState(authSuccessful = true)
        } catch (e: Exception) {
            _authorized.value = AuthModelState(authError = true)
        }
    }
}