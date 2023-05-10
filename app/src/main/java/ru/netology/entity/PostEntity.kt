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
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likesCount: Int = 0,
    val sharesCount: Int = 0,
    val viewsCount: Int = 0,
    val authorId: Long,
    @Embedded
    val attachment: Attachment? = null,
    val hidden: Boolean = false,
//    val videoUrl: String = "empty"
) {

    fun toDto() = Post(
        id = id,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likesCount = likesCount,
        sharesCount = sharesCount,
        viewsCount = viewsCount,
        authorId = authorId,
        attachment = attachment,
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                id = dto.id,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                content = dto.content,
                published = dto.published,
                likedByMe = dto.likedByMe,
                likesCount = dto.likesCount,
                sharesCount = dto.sharesCount,
                viewsCount = dto.viewsCount,
                authorId = dto.authorId,
                attachment = dto.attachment,
//                dto.videoUrl
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)