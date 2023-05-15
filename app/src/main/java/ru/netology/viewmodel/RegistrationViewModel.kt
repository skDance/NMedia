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
class RegistrationViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
) : ViewModel() {

    private val _registered = SingleLiveEvent<AuthModelState>()
    val registered: LiveData<AuthModelState>
        get() = _registered

    fun registration(login: String, pass: String, name: String) = viewModelScope.launch {
        _registered.value = AuthModelState(authLoading = true)
        try {
            val response = repository.register(login, pass, name)
            response.token?.let { appAuth.setAuth(response.id, response.token) }
            _registered.value = AuthModelState(authSuccessful = true)
        } catch (e: Exception) {
            _registered.value = AuthModelState(authError = true)
        }
    }
}