package com.smarttoolfactory.tutorial2_1flowbasics.data.db

import android.app.Application
import androidx.room.*
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import kotlinx.coroutines.flow.Flow


const val DATABASE_NAME = "post.db"

@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postDaoRxJava(): PostDaoRxJava
}

fun provideDatabase(application: Application): PostDatabase {
    return Room.databaseBuilder(
        application,
        PostDatabase::class.java,
        DATABASE_NAME
    ).build()
}

@Dao
interface PostDao {

    /*
        ***** Coroutines ******
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<PostEntity>): List<Long>

    @Delete
    suspend fun deletePost(entity: PostEntity): Int

    @Query("DELETE FROM post")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM post")
    suspend fun getPostCount(): Int

    /**
     * Get posts from database.
     *
     * *If database is empty returns empty list []
     */
    // Suspend
    @Query("SELECT * FROM post")
    suspend fun getPostList(): List<PostEntity>

    /**
     * Get post with specified id.
     *
     * * If post with id not in database returns NULL whether [PostEntity] is nullable or not
     */
    @Query("SELECT * FROM post WHERE id =:postId")
    suspend fun getPost(postId: Int): PostEntity?


    /*
        ***** Flow ******
    */


    /**
     * Get [Flow] of [List] of [PostEntity]
     *
     * * If database is empty returns an empty list []
     */
    // Flow
    @Query("SELECT * FROM post")
    fun getPostListFlow(): Flow<List<PostEntity>>

    /**
     * Get [Flow] of [PostEntity] with specified id.
     *
     * * If post with id not in database returns NULL
     */
    @Query("SELECT * FROM post WHERE id =:postId")
    fun getPostFlow(postId: Int): Flow<PostEntity?>

}

