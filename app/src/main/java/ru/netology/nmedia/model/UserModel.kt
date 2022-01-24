package ru.netology.nmedia.model

import ru.netology.nmedia.dto.AuthUser
import ru.netology.nmedia.utils.Utils

data class UserModel(
    val user: AuthUser = Utils.EmptyUser.emptyUser,
    val error: Boolean = false
)