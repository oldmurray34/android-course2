package ru.netology.nmedia.dto

import com.google.gson.annotations.SerializedName

data class Post (
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    @SerializedName("likedByMe")
    val likeByMe: Boolean = false,
    @SerializedName("likes")
    var numberOfLikes: Int,
    val showOrNot: Boolean = false,
    val ownedByMe: Boolean = false,
    val attachment: Attachment? = null
)