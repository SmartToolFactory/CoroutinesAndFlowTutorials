package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Post
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.PostApiCoroutines
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.convertToObjectFromJson
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest

import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response


class PostsRepositoryTest {

    private val postApiCoroutines: PostApiCoroutines = mockk<PostApiCoroutines>()

    private lateinit var postsRepository: PostsRepository


    private val postList by lazy {
        convertToObjectFromJson<List<Post>>(getResourceAsText(RESPONSE_JSON_PATH))
    }

    @Before
    fun setUp() {
        postsRepository = PostsRepository(postApiCoroutines)
    }

    @After
    fun tearDown() {
        clearMocks(postApiCoroutines)
    }

    @Test
    fun `given Http 200, should return result with success`() = runBlockingTest {

//        // GIVEN
//        val posts = postlist!!
//        coEvery { postApi.getPosts() } returns posts
//
//        // WHEN
//        val expected = postsRepository.getPostResult()
//
//        // THEN
//        Truth.assertThat(posts).isEqualTo(expected.data)
//        coVerify(exactly = 1) { postApi.getPosts() }

    }

    @Test
    fun `given network error occurred, should return result with error`() = runBlockingTest {

        // GIVEN
        coEvery { postApiCoroutines.getPosts() } throws Exception("Network Exception")

        // WHEN
        val expected = postsRepository.getPostResult()

        // THEN
        Truth.assertThat("Network Exception").isEqualTo(expected.error?.message)
        Truth.assertThat(expected.error).isInstanceOf(Exception::class.java)
        coVerify(exactly = 1) { postApiCoroutines.getPosts() }

    }


    @Test
    fun `given Http 200, should return result with success from response`() = runBlockingTest {

        // GIVEN
        val posts = postList!!
        coEvery { postApiCoroutines.getPostsResponse() } returns Response.success(200, posts)

        // WHEN
        val expected = postsRepository.getPostResultFromResponse()

        // THEN
        Truth.assertThat(posts).isEqualTo(expected.data)
        coVerify(exactly = 1) { postApiCoroutines.getPostsResponse() }

    }

/*    @Test
    fun `given network error occurred, should return result with error from response`() =
        runBlockingTest {

            // TODO MediaType.get() THROWS error, FIX it
            // GIVEN
            val errorResponseBody = ResponseBody.create(
                MediaType.get("application/json"),
                Gson().toJson(mapOf("cause" to "not sure"))
            )

            coEvery { postApi.getPostsResponse() } returns Response.error(500, errorResponseBody)

            // WHEN
            val expected = postsRepository.getPostResult()

            // THEN
            Truth.assertThat("Network Exception").isEqualTo(expected.error?.message)
            Truth.assertThat(expected.error).isInstanceOf(Exception::class.java)
            coVerify(exactly = 1) { postApi.getPostsResponse() }

        }*/

    companion object{
        private const val RESPONSE_JSON_PATH = "posts.json"
    }
}
