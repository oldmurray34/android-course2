package ru.netology.nmedia.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.AttachmentType

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    @ColumnInfo(name = "likeByMe")
    val likedByMe: Boolean,
    @ColumnInfo(name = "numberOfLikes")
    val likes: Int = 0,
    val showOrNot: Boolean = false,
    @Embedded
    var attachment: AttachmentEmbeddable?
) {
    fun toDto(): Post {
        return Post(
            id,
            author,
            authorAvatar,
            content,
            published,
            likedByMe,
            likes,
            showOrNot,
            attachment?.toDto()
        )
    }

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likeByMe,
                dto.numberOfLikes,
                true,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )

        fun fromApi(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likeByMe,
                dto.numberOfLikes,
                false,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )
    }
}

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}


fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
fun List<Post>.toApiEntity(): List<PostEntity> = map(PostEntity::fromApi)
