package ru.netology.repository

import ru.netology.dto.Post

interface PostRepository {
    fun getAll(callback: Callback<List<Post>>)
    fun likeById(post: Post, callback: Callback<Post>)
    fun shareById(id: Long)
    fun removeById(id: Long, callback: Callback<Post>)
    fun save(post: Post, callback: Callback<Post>)

    interface Callback<T> {
        fun onSuccess(data: T)
        fun onFailure(e: Exception)
    }
}