package ru.netology.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.R
import ru.netology.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        published = "21 мая в 18:36",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        likesCount = 999_999,
        sharesCount = 6095,
        viewsCount = 1099,
        likedByMe = false
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        if (post.likedByMe){
            post = post.copy(likedByMe = !post.likedByMe, likesCount = (post.likesCount - 1))
        } else {
            post = post.copy(likedByMe = !post.likedByMe, likesCount = (post.likesCount + 1))
        }
        data.value = post
    }

    override fun share() {
        post = post.copy(sharesCount = (post.sharesCount + 1))
        data.value = post
    }
}