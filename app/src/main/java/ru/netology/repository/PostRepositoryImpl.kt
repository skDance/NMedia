package ru.netology.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.dto.Post
import java.io.IOException
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

    override fun getAll(callback: PostRepository.Callback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onFailure(Exception(response.message))
                        return
                    }
                    try {
                        val data = response.body?.string()
                            ?: throw java.lang.RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(data, typeToken))
                    } catch (e: Exception) {
                        callback.onFailure(e)
                    }
                }
            })
    }

    override fun likeById(post: Post, callback: PostRepository.Callback<Post>) {
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
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onFailure(Exception(response.message))
                        return
                    }

                    val data =
                        response.body?.string() ?: throw java.lang.RuntimeException("body is null")
                    callback.onSuccess(gson.fromJson(data, Post::class.java))
                }
            })
    }

    override fun shareById(id: Long) {
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) callback.onFailure(Exception(response.message))
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) callback.onFailure(Exception(response.message))
                }
            })
    }
}