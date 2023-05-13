package ru.netology.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.api.Api
import ru.netology.dto.PushToken

class AppAuth private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authStateFlow = MutableStateFlow(AuthState())
    val authStateFlow = _authStateFlow

    init {
        var id: Long = 0L
        var token: String? = null

        token = prefs.getString(TOKEN, null)
        id = prefs.getLong(ID, 0L)

        if (id == 0L || token == null) {
            _authStateFlow.value = AuthState()
            prefs.edit {
                clear()
            }
        } else {
            _authStateFlow.value = AuthState(id = id, token = token)
        }
        sendPushToken()
    }

    @Synchronized
    fun clear() {
        _authStateFlow.value = AuthState()
        prefs.edit {
            clear()
        }
        sendPushToken()
    }

    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id = id, token = token)
        prefs.edit {
            putLong(ID, id)
            putString(TOKEN, token)
        }
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                Api.service.saveToken(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {

        const val ID = "id"
        const val TOKEN = "token"

        @Volatile
        private var INSTANCE: AppAuth? = null

        fun init(context: Context) {
            synchronized(this) {
                INSTANCE = AppAuth(context)
            }
        }

        fun getInstance(): AppAuth {
            return synchronized(this) {
                requireNotNull(INSTANCE) { "Make init" }
            }
        }
    }
}

data class AuthState(val id: Long = 0L, val token: String? = null)