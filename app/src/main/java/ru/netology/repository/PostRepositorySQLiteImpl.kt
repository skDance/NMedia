package ru.netology.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.dao.PostDao
import ru.netology.dto.Post

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {

    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else {
                if (it.likedByMe) it.copy(
                    likedByMe = !it.likedByMe,
                    likesCount = (it.likesCount - 1)
                ) else it.copy(likedByMe = !it.likedByMe, likesCount = (it.likesCount + 1))
            }
        }
        data.value = posts
        dao.likeById(id)
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(sharesCount = (it.sharesCount + 1))
        }
        data.value = posts
        dao.shareById(id)
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        dao.removeById(id)
    }

    override fun save(post: Post) {
        val id = post.id
        val saved = dao.save(post)
        posts = if (id == 0L) {
            listOf(saved) + posts
        } else {
            posts.map {
                if (it.id != id) it else saved
            }
        }
        data.value = posts
    }
}