package ru.netology.repository

import androidx.lifecycle.LiveData
import ru.netology.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(post: Post): Post
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
}