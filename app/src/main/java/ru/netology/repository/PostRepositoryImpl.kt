package ru.netology.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.api.ApiService
import ru.netology.auth.AuthState
import ru.netology.dao.PostDao
import ru.netology.dto.Attachment
import ru.netology.dto.AttachmentType
import ru.netology.dto.Media
import ru.netology.dto.Post
import ru.netology.entity.PostEntity
import ru.netology.entity.toEntity
import ru.netology.error.ApiError
import ru.netology.error.NetworkError
import ru.netology.error.UnknownError
import ru.netology.model.PhotoModel
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    ) : PostRepository {
    override val data = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            PostPagingSource(
                apiService
            )
        }
    ).flow

    override suspend fun getAll() {
        val response = apiService.getAll()

        if (!response.isSuccessful) throw java.lang.RuntimeException("api error")
        response.body() ?: throw java.lang.RuntimeException("body is null")
        postDao.insert(response.body()!!.map { PostEntity.fromDto(it) })
    }

    override fun getNewer(id: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10_000)
                val response = apiService.getNewer(id)
                val posts = response.body().orEmpty()

                emit(posts.size)

                postDao.insert(posts.toEntity().map {
                    it.copy(hidden = true)
                })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun showRecentEntries() {
        postDao.showAll()
    }

    override suspend fun likeById(post: Post) {
        postDao.likeById(post.id)
        try {
            val response = if (post.likedByMe) {
                apiService.dislikeById(post.id)
            } else {
                apiService.likeById(post.id)
            }
            if (!response.isSuccessful) throw java.lang.RuntimeException("api error")
            response.body() ?: throw java.lang.RuntimeException("body is null")
        } catch (e: Exception) {
            postDao.likeById(post.id)
        }

    }

    override fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(post: Post) {
        val oldPost = post
        postDao.removeById(post.id)

        try {
            val response = apiService.removeById(post.id)
            if (!response.isSuccessful) throw java.lang.RuntimeException("api error")
            response.body() ?: throw java.lang.RuntimeException("body is null")
        } catch (e: Exception) {
            postDao.insert(PostEntity.fromDto(oldPost))
        }

    }

    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, photo: PhotoModel) {
        try {
            val media = upload(photo)

            val response = apiService.save(
                post.copy(
                    attachment = Attachment(media.id, AttachmentType.IMAGE)
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun upload(photo: PhotoModel): Media {
        val response = apiService.uploadPhoto(
            MultipartBody.Part.createFormData("file", photo.file.name, photo.file.asRequestBody())
        )
        return response.body() ?: throw ApiError(response.code(), response.message())
    }

    override suspend fun singIn(login: String, pass: String): AuthState {
        val response = apiService.updateUser(login, pass)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return response.body() ?: throw ApiError(response.code(), response.message())
    }

    override suspend fun register(login: String, pass: String, name: String): AuthState {
        val response = apiService.registerUser(login, pass, name)
        if (!response.isSuccessful) throw ApiError(response.code(), response.message())
        return response.body() ?: throw ApiError(response.code(), response.message())
    }
}