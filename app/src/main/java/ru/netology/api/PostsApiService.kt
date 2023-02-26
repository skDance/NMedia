package ru.netology.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.dto.Post

private const val BASE_URL = "http://10.0.2.2:10999/api/slow/"

interface PostsApiService {
    @GET("posts")
    fun getAll(): Call<List<Post>>

    @POST("posts/{postId}/likes")
    fun likeById(@Path("postId") id: Long): Call<Post>

    @DELETE("posts/{postId}")
    fun removeById(@Path("postId") id: Long): Call<Post>

    @POST("posts")
    fun save(): Call<Post>
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