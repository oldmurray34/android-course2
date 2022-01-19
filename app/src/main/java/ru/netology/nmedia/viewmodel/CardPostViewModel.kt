
package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.CardPostModel
import ru.netology.nmedia.model.PostModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.Utils

class CardViewModel(application: Application) : AndroidViewModel(application) {

    private val empty = Post(
        id = 0,
        content = "",
        author = "",
        authorAvatar = "",
        likeByMe = false,
        published = "",
        numberOfLikes = 0,
        attachment = null
    )

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    private val _post = MutableLiveData(CardPostModel(loading = true, post = empty))
    val post: LiveData<CardPostModel>
        get() = _post

    fun getPostById(id: Long) = viewModelScope.launch {
        try {
            _post.value = CardPostModel(loading = true)
            _post.value = CardPostModel(repository.getPostById(id))
        } catch (e: Exception) {
            _post.value = CardPostModel(error = true, post = Utils.EmptyPost.emptyPost)
        }
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
            getPostById(id)
        } catch (e: Exception) {
            _post.value = CardPostModel(error = true)
        }
    }

    fun unlikeById(id: Long) = viewModelScope.launch {
        try {
            repository.unlikeById(id)
            getPostById(id)
        } catch (e: Exception) {
            _post.value = CardPostModel(error = true)
        }
    }

}