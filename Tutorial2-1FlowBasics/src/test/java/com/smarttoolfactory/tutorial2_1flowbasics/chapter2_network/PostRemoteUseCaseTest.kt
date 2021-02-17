package com.smarttoolfactory.tutorial2_1flowbasics.chapter2_network

import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial2_1flowbasics.base.BaseCoroutineJUnit5Test
import com.smarttoolfactory.tutorial2_1flowbasics.convertFromJsonToObjectList
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.DTOtoPostMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostDTO
import com.smarttoolfactory.tutorial2_1flowbasics.flow.test
import com.smarttoolfactory.tutorial2_1flowbasics.getResourceAsText
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


/**
 * Scenarios to test:
 *
 * 1- Network error:
 *      Don' map DTO to Post -> pass error to upper layer
 * 2- Data success:
 *      Map DTO to Post -> Pass Post list
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRemoteUseCaseTest : BaseCoroutineJUnit5Test() {

    private val postRemoteRepository = mockk<PostRemoteRepository>()
    private val dtoToPostMapper = mockk<DTOtoPostMapper>()

    private lateinit var useCase: PostRemoteUseCase

    companion object {
        val postDTOs = convertFromJsonToObjectList<PostDTO>(getResourceAsText("response.json"))!!
        val postList = convertFromJsonToObjectList<Post>(getResourceAsText("response.json"))!!
    }


    /**
     * ❌ This test fails if first flowOn(Dispatchers.IO) exist above the one flowOn(Dispatchers.DEFAULT)
     */
    @Test
    fun `given network error returned from repos, should throw exception`() =
        testCoroutineScope.runBlockingTest {

            println("testCoroutineScope: $testCoroutineScope, this: $this")

            // GIVEN
            every { postRemoteRepository.getPostFlow() } returns flow<List<PostDTO>> {
                emit(throw Exception("Network Exception"))
            }

            // WHEN
            var expected: Exception? = null
            useCase.getPostFlow()
                .catch { throwable: Throwable ->
                    expected = throwable as Exception
                    println("⏰ Expected: $expected")
                }
                .launchIn(this)

            // THEN
            println("⏰ TEST THEN")

            Truth.assertThat(expected).isNotNull()
            Truth.assertThat(expected).isInstanceOf(Exception::class.java)
            Truth.assertThat(expected?.message).isEqualTo("Network Exception")
        }

    /**
     * ✅ THIS TEST PASSES because it does not have Dispatcher with another thread like other method
     */
    @Test
    fun `given network error returned from repos with SIMPLE, should throw exception`() =
        testCoroutineScope.runBlockingTest {

            println("testCoroutineScope: $testCoroutineScope, this: $this")

            // GIVEN
            every { postRemoteRepository.getPostFlow() } returns flow<List<PostDTO>> {
                emit(throw Exception("Network Exception"))
            }

            // WHEN
            var expected: Exception? = null
            useCase.getPostFlowSimple()
                .catch { throwable: Throwable ->
                    expected = throwable as Exception
                    println("⏰ Expected: $expected")
                }
                .launchIn(this)

            // THEN
            println("⏰ TEST THEN")
            Truth.assertThat(expected?.message).isEqualTo("Network Exception")
        }


    /**
     * ✅ THIS TEST PASSES with Alternative 2 or when Alternative 1 with only one flowOn
     * or 2 flowOn with same dispatcher in [PostRemoteRepository.getPostFlow]
     */
    @Test
    fun `given data returned from api, should have data`() = testCoroutineScope.runBlockingTest {

        // GIVEN
        every { postRemoteRepository.getPostFlow() } returns flow { emit(postDTOs) }
        every { dtoToPostMapper.map(postDTOs) } returns postList

        val actual = postList

        // WHEN
        val expected = mutableListOf<Post>()
        // TODO Alternative 1, Not working when first flowOn uses Dispatchers.IO
//        useCase.getPostFlow().collect {postList->
//            println("⏰ Collect: ${postList.size}")
//
//            expected.addAll(postList)
//        }

        // TODO Alternative 2, Works fine

        val job = useCase.getPostFlow()
            .onEach { postList ->
                println("⏰ Collect: ${postList.size}, in thread: ${Thread.currentThread().name}")

                expected.addAll(postList)
            }
            .launchIn(testCoroutineScope)
        job.cancel()

        // THEN
        println("⏰ TEST THEN in thread: ${Thread.currentThread().name}")
        Truth.assertThat(expected).containsExactlyElementsIn(actual)
        verify(exactly = 1) { postRemoteRepository.getPostFlow() }
        verify(exactly = 1) { dtoToPostMapper.map(postDTOs) }
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()
        useCase =
            PostRemoteUseCase(postRemoteRepository = postRemoteRepository, mapper = dtoToPostMapper)
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(postRemoteRepository, dtoToPostMapper)
    }
}