package ru.netology.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.BuildConfig
import ru.netology.auth.AppAuth
import ru.netology.auth.AuthState
import ru.netology.dto.Media
import ru.netology.dto.Post

private const val BASE_URL = "http://10.0.2.2:10999/api/slow/"

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okhttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor { chain ->
        AppAuth.getInstance().authStateFlow.value.token?.let { token ->
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", token)
                .build()
                .apply { return@addInterceptor chain.proceed(this) }
        }
        return@addInterceptor chain.proceed(chain.request())
    }
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

interface PostsApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @POST("posts/{postId}/likes")
    suspend fun likeById(@Path("postId") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun uploadPhoto(@Part file: MultipartBody.Part): Response<Media>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{postId}")
    suspend fun removeById(@Path("postId") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthState>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<AuthState>
}

object PostApi {
    val service: PostsApiService by lazy {
        retrofit.create(PostsApiService::class.java)
    }
}