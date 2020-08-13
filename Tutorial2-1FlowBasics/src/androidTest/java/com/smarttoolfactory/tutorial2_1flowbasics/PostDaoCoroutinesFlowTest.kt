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
    private lateinit var measurementDao: PostDao

    /*
        Insert
     */
    @Test
    fun shouldInsertSinglePost() = runBlockingTest {

        // GIVEN
        val initialCount = measurementDao.getPostCount()

        // WHEN
        val insertedId = measurementDao.insert(postEntityList.first())

        // THEN
        val postCount = measurementDao.getPostCount()


        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(insertedId).isEqualTo(1)
        Truth.assertThat(postCount).isEqualTo(1)
    }

    @Test
    fun shouldInsertMultiplePosts() = runBlockingTest {

        // GIVEN
        val initialCount = measurementDao.getPostCount()

        // WHEN
        val ids = measurementDao.insert(postEntityList)

        // THEN
        val postCount = measurementDao.getPostCount()
        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(postCount).isEqualTo(postEntityList.size)
    }

    /*
        Get Post List Suspend
     */
    @Test
    fun givenDBEmptyShouldReturnEmptyList() = runBlockingTest {

        // GIVEN
        val postCount = measurementDao.getPostCount()

        // WHEN
        val postEntityList = measurementDao.getPostList()

        // THEN
        Truth.assertThat(postCount).isEqualTo(0)
        Truth.assertThat(postEntityList).hasSize(0)
    }

    @Test
    fun giveDBPopulatedShouldReturnCorrectMeasurements() = runBlockingTest {

        // GIVEN
        val expected = postEntityList[0]
        measurementDao.insert(expected)

        // WHEN
        val postEntityList = measurementDao.getPostList()

        // THEN
        val actual = postEntityList[0]
        Truth.assertThat(actual).isEqualTo(expected)
    }


    /*
        Get Post Suspend Nullable
     */
    @Test
    fun givenDBEmptyShouldReturnNullWithId() = runBlockingTest {

        // GIVEN
        val postCount = measurementDao.getPostCount()

        // WHEN
        val postEntity = measurementDao.getPost(1)

        // THEN
        Truth.assertThat(postCount).isEqualTo(0)
        Truth.assertThat(postEntity).isNull()
    }

    @Test
    fun givenDBEmptyShouldReturnFlowWithNull() = testCoroutineRule.runBlockingTest {

        // GIVEN

        // WHEN
        val testObserver = measurementDao.getPostFlow(1).test(this)

        // THEN


    }

    @Test
    fun givenDBPopulatedShouldReturnCorrectMeasurementWithId() = runBlockingTest {

        // GIVEN
        val expected = postEntityList[0]
        measurementDao.insert(expected)

        // WHEN
        val actual = measurementDao.getPost(1)

        // THEN
        Truth.assertThat(actual).isEqualTo(expected)
    }

    /*
        Get Post List Flow
     */


    /*
        Get Post Flow
     */

    /*
        Delete Suspend
     */
    @Test
    fun givenEveryMeasurementsDeletedShouldReturnEmptyList() = runBlockingTest {

        // GIVEN
        measurementDao.insert(postEntityList)
        val initialMeasurementCount = measurementDao.getPostCount()

        // WHEN
        measurementDao.deleteAll()

        // THEN
        val measurementCount = measurementDao.getPostCount()
        Truth.assertThat(initialMeasurementCount).isEqualTo(postEntityList.size)
        Truth.assertThat(measurementCount).isEqualTo(0)
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

        measurementDao = database.postDao()

    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        database.close()
    }


}