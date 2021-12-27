package ru.netology.nmedia.repository

import android.os.StrictMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.service.PostApiService
import java.io.IOException
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeListToken = object : TypeToken<List<Post>>() {}
    private val typePostToken = object : TypeToken<Post>() {}

//    companion object {
//        private const val BASE_URL = "http://10.0.2.2:9999"
//        private val jsonType = "application/json".toMediaType()
//    }

    override fun getAllAsync(callback: PostRepository.RepositoryCallback<List<Post>>) {
        PostApiService.api.getAllAsync().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    response.code()
                    response.errorBody()
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess(response.body()?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    override fun getPostAsync(callback: PostRepository.RepositoryCallback<Post>, id: Long) {
        PostApiService.api.getPostAsync(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    response.code()
                    response.errorBody()
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("Body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    override fun likeByIdAsync(callback: PostRepository.RepositoryCallback<Post>, id: Long) {
        PostApiService.api.likeByIdAsync(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    response.code()
                    response.errorBody()
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("Body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    override fun dislikeByIdAsync(callback: PostRepository.RepositoryCallback<Post>, id: Long) {
        PostApiService.api.dislikeByIdAsync(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    response.code()
                    response.errorBody()
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("Body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    override fun saveAsync(callback: PostRepository.RepositoryCallback<Post>, post: Post) {
        PostApiService.api.postCreation(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    response.code()
                    response.errorBody()
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("Body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

//    override fun updatePost(post: Post, callback: PostRepository.Callback<Post>) {
//        val request: Request = Request.Builder()
//            .post(gson.toJson(post).toRequestBody(jsonType))
//            .url("${BASE_URL}/api/posts")
//            .build()
//
//        return client.newCall(request)
//            .execute()
//            .use {
//                it.body?.string()
//            }
//            .let {
//                gson.fromJson(it, typePostToken.type)
//            }
//    }

    override fun removeByIdAsync(callback: PostRepository.OnRemoveCallback, id: Long) {
        PostApiService.api.deleteById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    response.code()
                    response.errorBody()
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    override fun findPostByIdAsync(callback: PostRepository.RepositoryCallback<Post>, id: Long) {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val request: Request = Request.Builder()
            .get()
            .url("${BuildConfig.BASE_URL}/api/posts/$id")
            .build()

        return client.newCall(request)
            .execute()
            .use {
                it.body?.string()
            }
            .let {
                gson.fromJson(it, typePostToken.type)
            }
    }
}
