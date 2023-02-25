package ru.netology.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.dto.Post
import ru.netology.model.FeedModel
import ru.netology.repository.PostRepository
import ru.netology.repository.PostRepositoryImpl
import ru.netology.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
    published = "",
    content = "",
    likesCount = 0,
    sharesCount = 0,
    viewsCount = 0,
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    val openPostById: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAll(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(data: List<Post>) {
                _data.postValue(FeedModel(posts = data, empty = data.isEmpty()))
            }

            override fun onFailure(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun likeById(post: Post) {
        val old = data.value?.posts
        repository.likeById(post, object : PostRepository.Callback<Post> {
            override fun onSuccess(data: Post) {
                val posts = old?.map { if (it.id != post.id) it else data }
                _data.postValue(_data.value?.copy(posts = posts))
            }

            override fun onFailure(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

    fun shareById(id: Long) = repository.shareById(id)

    fun removeById(id: Long) {
        val old = data.value?.posts.orEmpty()

        repository.removeById(id, object : PostRepository.Callback<Post> {
            override fun onSuccess(data: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onFailure(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.save(it, object : PostRepository.Callback<Post> {
                override fun onSuccess(data: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onFailure(e: Exception) {}
            })
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }
}