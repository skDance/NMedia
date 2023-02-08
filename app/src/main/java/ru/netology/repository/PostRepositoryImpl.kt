package ru.netology.repository

import androidx.lifecycle.Transformations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.dao.PostDao
import ru.netology.dto.Post
import ru.netology.entity.PostEntity
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:10999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw java.lang.RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken)
            }
    }

    override fun likeById(post: Post): Post {
        val request: Request = if (post.likedByMe) {
            Request.Builder()
                .delete()
                .url("${BASE_URL}/api/slow/posts/${post.id}/likes")
                .build()
        } else {
            Request.Builder()
                .post("".toRequestBody(jsonType))
                .url("${BASE_URL}/api/slow/posts/${post.id}/likes")
                .build()
        }
        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw java.lang.RuntimeException("body is null") }
            .let { gson.fromJson(it, Post::class.java) }
    }

    override fun shareById(id: Long) {
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}