package ru.netology.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.dto.Post

private const val BASE_URL = "http://10.0.2.2:10999/api/slow/"

interface PostsApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @POST("posts/{postId}/likes")
    suspend fun likeById(@Path("postId") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{postId}")
    suspend fun removeById(@Path("postId") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>
}

object PostApi {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val retrofitService: PostsApiService by lazy {
        retrofit.create()
    }
}