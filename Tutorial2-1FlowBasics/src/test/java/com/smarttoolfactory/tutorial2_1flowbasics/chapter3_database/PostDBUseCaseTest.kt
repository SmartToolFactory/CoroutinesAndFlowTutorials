package com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database

import com.smarttoolfactory.tutorial2_1flowbasics.base.BaseCoroutineJUnit5Test
import com.smarttoolfactory.tutorial2_1flowbasics.convertFromJsonToObjectList
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.EntityToPostMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.PostToEntityMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import com.smarttoolfactory.tutorial2_1flowbasics.getResourceAsText
import io.mockk.clearMocks
import io.mockk.mockk
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