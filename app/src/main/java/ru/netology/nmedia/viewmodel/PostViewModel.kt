package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "sber",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.RepositoryCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Throwable) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.saveAsync(object: PostRepository.RepositoryCallback<Post> {
                override fun onSuccess(value: Post) {
                    _postCreated.postValue(Unit)
                    edited.value = empty
                }

                override fun onError(e: Throwable) {
                    _data.postValue(FeedModel(error = true))
                }
            }, it)
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        val post = _data.value?.posts?.find { it.id == id }
        if (post?.likedByMe == false) {
            repository.likeByIdAsync(object : PostRepository.RepositoryCallback<Post> {
                override fun onSuccess(post: Post) {
                    val newPosts = _data.value?.posts?.map {
                        if (it.id != post.id) it else it.copy(
                            likedByMe = !it.likedByMe,
                            likes = it.likes + 1
                        )
                    }
                    _data.postValue(
                        newPosts?.let { _data.value?.copy(posts = it) }
                    )
                }

                override fun onError(e: Throwable) {
                    _data.postValue(FeedModel(error = true))
                }
            }, id)
        } else {
            repository.dislikeByIdAsync(object : PostRepository.RepositoryCallback<Post> {
                override fun onSuccess(post: Post) {
                    val newPosts = _data.value?.posts?.map {
                        if (it.id != post.id) it else it.copy(
                            likedByMe = !it.likedByMe,
                            likes = it.likes - 1
                        )
                    }
                    _data.postValue(
                        newPosts?.let { _data.value?.copy(posts = it) }
                    )
                }

                override fun onError(e: Throwable) {
                    _data.postValue(FeedModel(error = true))
                }
            }, id)
        }
    }

    fun removeById(id: Long) {
        repository.removeByIdAsync(object : PostRepository.OnRemoveCallback {
            override fun onSuccess() {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onError(e: Throwable) {
                _data.postValue(FeedModel(error = true))
            }
        }, id)
    }
}
