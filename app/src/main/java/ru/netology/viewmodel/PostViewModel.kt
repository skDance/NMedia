package ru.netology.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.db.AppDb
import ru.netology.dto.Post
import ru.netology.model.FeedModel
import ru.netology.model.FeedModelState
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
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    val data: LiveData<FeedModel> = repository.data().map { FeedModel(it, it.isEmpty()) }

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

    fun loadPosts() = viewModelScope.launch {
        try {
            _state.value = FeedModelState(loading = true)
            repository.getAll()
            _state.value = FeedModelState()
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
        }

    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _state.value = FeedModelState(refreshing = true)
            repository.getAll()
            _state.value = FeedModelState()
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
        }

    }

    fun likeById(post: Post) = viewModelScope.launch {
        try {
            repository.likeById(post)
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
        }
    }

    fun shareById(id: Long) = repository.shareById(id)

    fun removeById(post: Post) = viewModelScope.launch {
        try {
            repository.removeById(post)
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
        }
    }

    fun save() = viewModelScope.launch {
        edited.value?.let {
            repository.save(it)
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