package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class PostModel(
    val post: Post = Post(
        id = 0,
        content = "",
        author = "",
        authorAvatar = "",
        likeByMe = false,
        published = "",
        numberOfLikes = 0
    ),
    val empty: Boolean = false,
    val loading: Boolean = false,
    val error: Boolean = false
    )
