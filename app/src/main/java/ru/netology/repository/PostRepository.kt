package ru.netology.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.dto.Post

interface PostRepository {
    fun data(): Flow<List<Post>>
    suspend fun getAll()
    fun getNewer(id: Long): Flow<Int>
    suspend fun showRecentEntries()
    suspend fun likeById(post: Post)
    fun shareById(id: Long)
    suspend fun removeById(post: Post)
    suspend fun save(post: Post)
}