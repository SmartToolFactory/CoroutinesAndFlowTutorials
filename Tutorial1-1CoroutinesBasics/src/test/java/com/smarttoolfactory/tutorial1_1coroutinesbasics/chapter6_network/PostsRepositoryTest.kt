package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import com.google.common.truth.Truth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Post
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.PostApi
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.InputStream
import java.nio.charset.StandardCharsets


class PostsRepositoryTest {

    private val postApi: PostApi = mockk<PostApi>()

    private lateinit var postsRepository: PostsRepository


    private val postlist by lazy {
        convertToObjectsFromString<List<Post>>(getStringFromResources("posts.json"))
    }


    @Before
    fun setUp() {
        postsRepository = PostsRepository(postApi)
    }

    @After
    fun tearDown() {
        clearMocks(postApi)
    }

    @Test
    fun `Given Http 200, should return result with success`() = runBlockingTest {

        // GIVEN
        val posts = postlist!!
        coEvery { postApi.getPosts() } returns posts

        // WHEN
        val expected = postsRepository.getPostResult()

        // THEN
        Truth.assertThat(posts).isEqualTo(expected.data)
        coVerify(exactly = 1) { postApi.getPosts() }

    }

    @Test
    fun `Given network error occurred, should return result with error`() = runBlockingTest {

        // GIVEN
        coEvery { postApi.getPosts() } throws Exception("Network Exception")

        // WHEN
        val expected = postsRepository.getPostResult()

        // THEN
        Truth.assertThat("Network Exception").isEqualTo(expected.error?.message)
        Truth.assertThat(expected.error).isInstanceOf(Exception::class.java)
        coVerify(exactly = 1) { postApi.getPosts() }

    }


    @Test
    fun `Given Http 200, should return result with success from response`() = runBlockingTest {

        // GIVEN
        val posts = postlist!!
        coEvery { postApi.getPostsResponse() } returns Response.success(200, posts)


        // WHEN
        val expected = postsRepository.getPostResultFromResponse()

        // THEN
        Truth.assertThat(posts).isEqualTo(expected.data)
        coVerify(exactly = 1) { postApi.getPostsResponse() }

    }

/*    @Test
    fun `Given network error occurred, should return result with error from response`() =
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


    private fun getResourceAsText(path: String): String {
        return object {}.javaClass.classLoader!!.getResource(path)!!.readText()
    }

    private fun getStringFromResources(fileName: String): String {
        val inputStream: InputStream = this.javaClass.classLoader!!.getResourceAsStream(fileName)
        val source = inputStream.source().buffer()

        return source.readString(StandardCharsets.UTF_8)
    }


}


inline fun <reified T> convertToObjectsFromString(input: String): T? {

    return Gson().fromJsonWithType<T>(input)
}

inline fun <reified T> Gson.fromJsonWithType(json: String): T? =
    fromJson<T>(json, object : TypeToken<T>() {}.type)
