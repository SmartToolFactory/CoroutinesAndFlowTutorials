package com.smarttoolfactory.tutorial2_1flowbasics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDatabase
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import com.smarttoolfactory.tutorial2_1flowbasics.flow.test
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostDaoCoroutinesFlowTest {

    companion object {
        val postEntityList =
            convertFromJsonToObjectList<PostEntity>(getResourceAsText("response.json"))!!
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    private lateinit var database: PostDatabase

    /**
     * This is the SUT
     */
    private lateinit var postDao: PostDao

    /*
        Insert
     */

    @Test
    fun shouldInsertSinglePost() = runBlockingTest {

        // GIVEN
        val initialCount = postDao.getPostCount()

        // WHEN
        val insertedId = postDao.insert(postEntityList.first())

        // THEN
        val postCount = postDao.getPostCount()


        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(insertedId).isEqualTo(1)
        Truth.assertThat(postCount).isEqualTo(1)
    }

    @Test
    fun shouldInsertMultiplePosts() = runBlockingTest {

        // GIVEN
        val initialCount = postDao.getPostCount()

        // WHEN
        val ids = postDao.insert(postEntityList)

        // THEN
        val postCount = postDao.getPostCount()
        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(postCount).isEqualTo(postEntityList.size)
    }

    /*
        Get Post List Suspend
     */

    @Test
    fun givenDBEmptyShouldReturnEmptyList() = runBlockingTest {

        // GIVEN
        val postCount = postDao.getPostCount()

        // WHEN
        val postEntityList = postDao.getPostList()

        // THEN
        Truth.assertThat(postCount).isEqualTo(0)
        Truth.assertThat(postEntityList).hasSize(0)
    }

    @Test
    fun giveDBPopulatedShouldReturnCorrectMeasurements() = runBlockingTest {

        // GIVEN
        val expected = postEntityList[0]
        postDao.insert(expected)

        // WHEN
        val postEntityList = postDao.getPostList()

        // THEN
        val actual = postEntityList[0]
        Truth.assertThat(actual).isEqualTo(expected)
    }


    /*
        Get Post Suspend Null for empty DB
     */

    @Test
    fun givenDBEmptyShouldReturnNullWithId() = runBlockingTest {

        // GIVEN
        val postCount = postDao.getPostCount()

        // WHEN
        val postEntity = postDao.getPost(1)

        // THEN
        Truth.assertThat(postCount).isEqualTo(0)
        Truth.assertThat(postEntity).isNull()
    }


    /*
       Get Post Suspend Null for empty DB
     */
    @Test
    fun givenDBPopulatedShouldReturnCorrectMeasurementWithId() = runBlockingTest {

        // GIVEN
        val expected = postEntityList[0]
        postDao.insert(expected)

        // WHEN
        val actual = postDao.getPost(1)

        // THEN
        Truth.assertThat(actual).isEqualTo(expected)
    }
    /*
      Delete Suspend
     */

    @Test
    fun givenEveryMeasurementsDeletedShouldReturnEmptyList() = runBlockingTest {

        // GIVEN
        postDao.insert(postEntityList)
        val initialMeasurementCount = postDao.getPostCount()

        // WHEN
        postDao.deleteAll()

        // THEN
        val measurementCount = postDao.getPostCount()
        Truth.assertThat(initialMeasurementCount).isEqualTo(postEntityList.size)
        Truth.assertThat(measurementCount).isEqualTo(0)
    }

    /*
        Get Post or NULL Flow
     */

    /**
     * ✅ Passes if job is canceled. It does not matter if testCoroutineRule, job needs to be canceled
     */
    @Test
    fun givenDBEmptyShouldReturnFlowWithNull() = runBlockingTest {

        // GIVEN
        val expected: PostEntity? = null

        // WHEN
        var actual: PostEntity? = null

        val job = launch {
            postDao.getPostFlow(1)
                .collect {
                    actual = it
                }
        }

        job.cancelAndJoin()

        // THEN
        Truth.assertThat(expected).isEqualTo(actual)

    }

    @Test
    fun givenDBEmptyShouldReturnFlowWithNullWithTestObserver() = testCoroutineRule.runBlockingTest {

        // GIVEN
        val expected: PostEntity? = null

        // WHEN
        val testObserver = postDao.getPostFlow(1).test(this)

        // THEN
        testObserver.assertValues {
            it.first() == expected
        }.dispose()
    }


    /**
     * ✅ Passes if job is canceled. It does not matter if testCoroutineRule, job needs to be canceled
     */
    @Test
    fun givenDBPopulatedShouldReturnSinglePostWithFlowW() = testCoroutineRule.runBlockingTest {

        // GIVEN
        val expected = postEntityList[0]
        postDao.insert(expected)

        // WHEN
        var actual: PostEntity? = null
        val job = launch {
            postDao.getPostFlow(1).collect {
                actual = it
            }
        }

        // THEN
        Truth.assertThat(actual).isEqualTo(expected)
        job.cancelAndJoin()
    }

    @Test
    fun givenDBPopulatedShouldReturnSinglePostWithFlowTestObserver() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            val expected = postEntityList[0]
            postDao.insert(expected)

            // WHEN
            val testObserver = postDao.getPostFlow(1).test(this)

            val actual = testObserver.values().first()


            // THEN
            Truth.assertThat(actual).isEqualTo(expected)
            testObserver.dispose()
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

        postDao = database.postDao()

    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        database.close()
    }
}