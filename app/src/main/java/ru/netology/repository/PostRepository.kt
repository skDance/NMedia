package ru.netology.repository

import androidx.lifecycle.LiveData
import ru.netology.dto.Post

interface PostRepository {
    fun data(): LiveData<List<Post>>
    suspend fun getAll()
    suspend fun likeById(post: Post)
    fun shareById(id: Long)
    suspend fun removeById(post: Post)
    suspend fun save(post: Post)
}