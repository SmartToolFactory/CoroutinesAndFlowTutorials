package com.smarttoolfactory.tutorial2_1flowbasics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.rxjava3.EmptyResultSetException
import androidx.test.core.app.ApplicationProvider
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDaoRxJava
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDatabase
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostDaoRxJavaTest {

    companion object {
        val postEntityList =
            convertFromJsonToObjectList<PostEntity>(getResourceAsText("response.json"))!!
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: PostDatabase

    /**
     * This is the SUT
     */
    private lateinit var postDao: PostDaoRxJava


    /**
     * Given empty
     */
    @Test
    fun givenEmptyDBGetPostByIdSingleThrowsEmptyResultSetException() {

//        postDao.insertPost(postEntityList[0]).blockingAwait()

        postDao.getPostByIdSingleNullable(1)
            .test()
            .assertError(EmptyResultSetException::class.java)
            .assertNotComplete()
            .dispose()

        postDao.getPostByIdSingle(1)
            .test()
            .assertError(EmptyResultSetException::class.java)
            .assertNotComplete()
            .dispose()

    }


    /**
     * When Empty list is present, Maybe completes but does NOT return anything
     */
    @Test
    fun givenEmptyDBGetPostByIdMaybeOnlyCompletes() {

//        postDao.insertPost(postEntityList[0]).blockingAwait()

        postDao.getPostByIdMaybe(1)
            .test()
            .assertNoErrors()
            .assertComplete()
            .dispose()
    }


    /*
        If return type from DB operation is List then both Single, Maybe and Observable returns
        EMPTY List of type
     */
    @Test
    fun givenEmptyDBGetPostListSingleReturnsEmptyList() {

//        postDao.insertPost(postEntityList[0]).blockingAwait()

        postDao.getPostListByIdSingle(1)
            .test()
            .assertValue {
                it.isEmpty()
            }
            .assertComplete()
            .dispose()
    }

    @Test
    fun givenEmptyDBGetPostListMaybeReturnsEmptyList() {

//        postDao.insertPost(postEntityList[0]).blockingAwait()

        postDao.getPostListByIdMaybe(1)
            .test()
            .assertValue {
                it.isEmpty()
            }
            .assertComplete()
            .dispose()
    }

    @Test
    fun givenEmptyDBGetPostLisObservableReturnsEmptyList() {

//        postDao.insertPost(postEntityList[0]).blockingAwait()

        postDao.getPostListById(1)
            .test()
            .assertValue {
                it.isEmpty()
            }
            .assertNotComplete()
            .dispose()
    }


    @Before
    fun setUp() {

        // using an in-memory database because the information stored here disappears after test
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), PostDatabase::class.java
        )
            // allowing main thread queries, just for testing
//            .allowMainThreadQueries()
            .build()

        postDao = database.postDaoRxJava()

    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        database.close()
    }
}