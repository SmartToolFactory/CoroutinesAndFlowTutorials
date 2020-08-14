package com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database

import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial2_1flowbasics.base.BaseCoroutineJUnit5Test
import com.smarttoolfactory.tutorial2_1flowbasics.convertFromJsonToObjectList
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import com.smarttoolfactory.tutorial2_1flowbasics.flow.test
import com.smarttoolfactory.tutorial2_1flowbasics.getResourceAsText
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.lang.Exception

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostDBRepositoryTest : BaseCoroutineJUnit5Test() {

    private val postDao = mockk<PostDao>()
    private lateinit var postDBRepository: PostDBRepository

    private val postEntityList by lazy {
        convertFromJsonToObjectList<PostEntity>(getResourceAsText("response.json"))!!
    }

    @Test
    fun `given empty list returned from  DB, should return empty list`() =
        testCoroutineScope.runBlockingTest {

            // GIVEN
            every { postDBRepository.getPostListFlow() } returns flow { emit(listOf()) }

            // WHEN
            val testObserver = postDBRepository.getPostListFlow().test(this)

            // THEN
            val actual = testObserver.values()[0]
            Truth.assertThat(actual.size).isEqualTo(0)
            testObserver.dispose()
        }

    @Test
    fun `given data list returned from  DB, should return data list`() =
        testCoroutineScope.runBlockingTest {

            // GIVEN
            every { postDBRepository.getPostListFlow() } returns flow { emit(postEntityList) }

            // WHEN
            val testObserver = postDBRepository.getPostListFlow().test(this)

            // THEN
            val actual = testObserver.values()[0]
            Truth.assertThat(actual.size).isEqualTo(100)
            Truth.assertThat(actual).containsExactlyElementsIn(postEntityList)
            testObserver.dispose()
        }

    @BeforeEach
    override fun setUp() {
        super.setUp()
        postDBRepository = PostDBRepository(postDao)
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(postDao)
    }
}