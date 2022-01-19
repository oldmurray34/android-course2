package ru.netology.nmedia.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.AttachmentType

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE showOrNot = 1  ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) FROM PostEntity WHERE showOrNot = 0")
    suspend fun count(): Int

    @Query("UPDATE PostEntity Set showOrNot = :showOrNot WHERE showOrNot is not :showOrNot")
    suspend fun showOrNot(showOrNot: Boolean)

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getPostById(id: Long): PostEntity

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query(
        """
           UPDATE PostEntity SET
               numberOfLikes = numberOfLikes + CASE WHEN likeByMe THEN -1 ELSE 1 END,
               likeByMe = CASE WHEN likeByMe THEN 0 ELSE 1 END
           WHERE id = :id;
        """
    )
    suspend fun likeById(id: Long)


    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun deleteById(id: Long)
}

class Converters {
        @TypeConverter
        fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)

        @TypeConverter
        fun fromAttachment(value: AttachmentType) = value.name
}
