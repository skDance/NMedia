package ru.netology.repository

import androidx.lifecycle.Transformations
import ru.netology.dao.PostDao
import ru.netology.dto.Post
import ru.netology.entity.PostEntity

class PostRepositoryImpl(
    private val dao: PostDao,
) : PostRepository {

    override fun getAll() = Transformations.map(dao.getAll()) { list ->
        list.map {
            Post(
                it.id,
                it.author,
                it.published,
                it.content,
                it.likedByMe,
                it.likesCount,
                it.sharesCount,
                it.viewsCount,
                it.videoUrl
            )
        }
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }
}