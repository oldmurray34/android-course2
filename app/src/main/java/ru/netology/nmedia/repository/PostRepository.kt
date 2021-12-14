package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun dislikeById(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)

    fun getAllAsync(callback: RepositoryCallback<List<Post>>)

    fun likeByIdAsync(callback: RepositoryCallback<Post>, id: Long)

    fun dislikeByIdAsync(callback: RepositoryCallback<Post>, id: Long)

    fun saveAsync(callback: RepositoryCallback<Post>, post: Post)

    fun removeByIdAsync(callback: OnRemoveCallback, id: Long)

    interface RepositoryCallback<T> {
        fun onSuccess(value: T)

        fun onError(e: Throwable)
    }

    interface OnRemoveCallback {
        fun onSuccess()

        fun onError(e: Throwable)
    }
}
