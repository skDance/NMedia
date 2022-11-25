package ru.netology.repository

import androidx.lifecycle.LiveData
import ru.netology.dto.Post

interface PostRepository {
    fun get(): LiveData<Post>
    fun like()
    fun share()
}