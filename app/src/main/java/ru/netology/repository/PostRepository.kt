package ru.netology.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.auth.AuthState
import ru.netology.dto.Post
import ru.netology.model.PhotoModel

interface PostRepository {
    fun data(): Flow<List<Post>>
    suspend fun getAll()
    fun getNewer(id: Long): Flow<Int>
    suspend fun showRecentEntries()
    suspend fun likeById(post: Post)
    fun shareById(id: Long)
    suspend fun removeById(post: Post)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, photo: PhotoModel)
    suspend fun singIn(login: String, pass: String): AuthState
    suspend fun register(login: String, pass: String, name: String): AuthState
}