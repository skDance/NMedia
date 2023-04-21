package ru.netology.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.dto.Attachment
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
    @Embedded
    val attachment: Attachment? = null,
    val hidden: Boolean = false,
//    val videoUrl: String = "empty"
) {

    fun toDto() = Post(
        id,
        author,
        content,
        published,
        content,
        likedByMe,
        likesCount,
        sharesCount,
        viewsCount,
        attachment
    )

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
                dto.attachment,
//                dto.videoUrl
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)