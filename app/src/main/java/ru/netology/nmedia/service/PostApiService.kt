package ru.netology.nmedia.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .addInterceptor(logging)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

interface PostApi {
    @GET("posts")
    fun getAllAsync() : Call<List<Post>>

    @GET("posts/{id}")
    fun getPostAsync(@Path("id") id: Long): Call<Post>

    @POST("posts")
    fun postCreation(@Body post: Post): Call<Post>

    @POST("posts/{id}/likes")
    fun likeByIdAsync(@Path("id") id: Long): Call<Post>

    @DELETE("posts/{id}/likes")
    fun dislikeByIdAsync(@Path("id") id: Long): Call<Post>

    @DELETE("posts/{id}")
    fun deleteById(@Path("id") id: Long): Call<Unit>
}

object PostApiService {
    val api : PostApi by lazy(retrofit::create)
}