package ru.netology.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likesCount: Int = 0,
    val sharesCount: Int = 0,
    val viewsCount: Int = 0,
//    val videoUrl: String = "empty"
) {

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likesCount,
                dto.sharesCount,
                dto.viewsCount,
//                dto.videoUrl
            )
    }
}