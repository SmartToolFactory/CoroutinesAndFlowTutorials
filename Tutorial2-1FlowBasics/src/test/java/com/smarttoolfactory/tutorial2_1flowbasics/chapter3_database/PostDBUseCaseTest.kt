package com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database

import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial2_1flowbasics.base.BaseCoroutineJUnit5Test
import com.smarttoolfactory.tutorial2_1flowbasics.convertFromJsonToObjectList
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.EntityToPostMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.PostToEntityMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import com.smarttoolfactory.tutorial2_1flowbasics.flow.test
import com.smarttoolfactory.tutorial2_1flowbasics.getResourceAsText
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostDBUseCaseTest : BaseCoroutineJUnit5Test() {

    private val postDBRepository = mockk<PostDBRepository>()
    private val entityToPostMapper = mockk<EntityToPostMapper>()
    private val postToEntityMapper = mockk<PostToEntityMapper>()

    private lateinit var postDBUseCase: PostDBUseCase

    private val postEntityList by lazy {
        convertFromJsonToObjectList<PostEntity>(getResourceAsText("response.json"))!!
    }

    private val postList by lazy {
        convertFromJsonToObjectList<Post>(getResourceAsText("response.json"))!!
    }

    @Test
    fun `given empty list returned from repo, should return an empty list`() =
        testCoroutineScope.runBlockingTest {

            // GIVEN
            every { postDBRepository.getPostListFlow() } returns flow { emit(listOf<PostEntity>()) }
            every { entityToPostMapper.map(listOf()) } returns listOf()

            // WHEN
            val testObserver = postDBUseCase.getPostListFlow().test(this)

            // THEN
            val actual = testObserver.values()[0]
            Truth.assertThat(actual.size).isEqualTo(0)
            verify(exactly = 1) { entityToPostMapper.map(listOf()) }

            testObserver.dispose()
        }


    @Test
    fun `given post list  from repo, should return a list`() =
        testCoroutineScope.runBlockingTest {

            // GIVEN
            every { postDBRepository.getPostListFlow() } returns flow { emit(postEntityList) }
            every { entityToPostMapper.map(postEntityList) } returns postList

            // WHEN
            val testObserver = postDBUseCase.getPostListFlow().test(this)

            // THEN
            val actual = testObserver.values()[0]
            Truth.assertThat(actual.size).isEqualTo(postEntityList.size)
            verify(exactly = 1) { entityToPostMapper.map(postEntityList) }

            testObserver.dispose()

        }

    @BeforeEach
    override fun setUp() {
        super.setUp()
        postDBUseCase = PostDBUseCase(postDBRepository, entityToPostMapper, postToEntityMapper)
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(postDBRepository, entityToPostMapper, postToEntityMapper)
    }
}