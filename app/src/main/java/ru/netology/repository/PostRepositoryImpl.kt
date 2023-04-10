package ru.netology.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.netology.api.PostApi
import ru.netology.dao.PostDao
import ru.netology.dto.Post
import ru.netology.entity.PostEntity
import ru.netology.entity.toEntity
import ru.netology.error.ApiError
import ru.netology.error.NetworkError
import ru.netology.error.UnknownError
import java.io.IOException

class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {
    override fun data() = postDao.getAll().map { it.map(PostEntity::toDto) }

    override suspend fun getAll() {
        val response = PostApi.retrofitService.getAll()

        if (!response.isSuccessful) throw java.lang.RuntimeException("api error")
        response.body() ?: throw java.lang.RuntimeException("body is null")
        postDao.insert(response.body()!!.map { PostEntity.fromDto(it) })
    }

    override fun getNewer(id: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10_000)
                val response = PostApi.retrofitService.getNewer(id)
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
                PostApi.retrofitService.dislikeById(post.id)
            } else {
                PostApi.retrofitService.likeById(post.id)
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
            val response = PostApi.retrofitService.removeById(post.id)
            if (!response.isSuccessful) throw java.lang.RuntimeException("api error")
            response.body() ?: throw java.lang.RuntimeException("body is null")
        } catch (e: Exception) {
            postDao.insert(PostEntity.fromDto(oldPost))
        }

    }

    override suspend fun save(post: Post) {
        try {
            val response = PostApi.retrofitService.save(post)
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

}