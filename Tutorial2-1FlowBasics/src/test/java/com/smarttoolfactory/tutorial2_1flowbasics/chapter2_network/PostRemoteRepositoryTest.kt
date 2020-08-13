package com.smarttoolfactory.tutorial2_1flowbasics.chapter2_network

import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial2_1flowbasics.convertFromJsonToObjectList
import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApi
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostDTO
import com.smarttoolfactory.tutorial2_1flowbasics.flow.test
import com.smarttoolfactory.tutorial2_1flowbasics.getResourceAsText
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*

/**
 * Check out UseCase and ViewModel tests
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRemoteRepositoryTest {

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    private val postApi = mockk<PostApi>()

    private lateinit var postRemoteRepository: PostRemoteRepository

    private val postList by lazy {
        convertFromJsonToObjectList<PostDTO>(getResourceAsText("response.json"))!!
    }

    @Test
    fun `given network error returned from api, should throw exception`() =
        testCoroutineScope.runBlockingTest {

            /*
                🔥testCoroutine scope: kotlinx.coroutines.test.TestCoroutineScopeImpl@10cf09e8,
                                 this: kotlinx.coroutines.test.TestCoroutineScopeImpl@1921ad94
             */
            println("testCoroutine scope: ${testCoroutineScope}, this: $this")

            // GIVEN
            coEvery { postApi.getPosts() } throws Exception("Network Exception")

            // WHEN
            var expected: Exception? = null

            // TODO Alternative 1
//            try {
//                postRemoteRepository.getPostFlow().collect()
//            } catch (e: Exception) {
//                expected = e
//            }

            // TODO Alternative 2
//            postRemoteRepository.getPostFlow()
//                .catch { throwable: Throwable ->
//                    expected = throwable as Exception
//                }
//                .launchIn(this)

            // THEN
//            Truth.assertThat(expected).isNotNull()
//            Truth.assertThat(expected).isInstanceOf(Exception::class.java)
//            Truth.assertThat(expected?.message).isEqualTo("Network Exception")

            // TODO Alternative 3

            postRemoteRepository.getPostFlow()
                .test(testCoroutineScope)
//                .assertError(Exception("Network Exception"))
                .assertError {
                    it.message == "Network Exception"
                }
                .dispose()
        }


    /**
     *  ❌ This test FAILS ???
     */
    @Test
    fun `given network error returned from api, should throw exception alternative`() =
        testCoroutineScope.runBlockingTest {

            // GIVEN
            coEvery { postApi.getPosts() } throws Exception("Network Exception")

            // WHEN
            val exception: Exception = assertThrows {
                println("In assetThrows thread: ${Thread.currentThread().name}")
                postRemoteRepository.getPostFlow()
                    .launchIn(this)
//                throw Exception("Network Exception")
            }

            // THEN
            Truth.assertThat(exception.message).isEqualTo("Network Exception")
        }


    @Test
    fun `given data returned from api, should have data`() = testCoroutineScope.runBlockingTest {

        // GIVEN
        coEvery { postApi.getPosts() } returns postList
        val actual = postList

        // WHEN
        val expected = mutableListOf<PostDTO>()
        postRemoteRepository.getPostFlow().collect {
            expected.addAll(it)
        }

        // THEN
        Truth.assertThat(expected).containsExactlyElementsIn(actual)
    }

    @BeforeEach
    fun setUp() {
        // provide the scope explicitly, in this example using a constructor parameter
        Dispatchers.setMain(testCoroutineDispatcher)

        postRemoteRepository = PostRemoteRepository(postApi)
    }

    @AfterEach
    fun tearDown() {

        Dispatchers.resetMain()
        try {
            testCoroutineDispatcher.cleanupTestCoroutines()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        clearMocks(postApi)
    }
}
