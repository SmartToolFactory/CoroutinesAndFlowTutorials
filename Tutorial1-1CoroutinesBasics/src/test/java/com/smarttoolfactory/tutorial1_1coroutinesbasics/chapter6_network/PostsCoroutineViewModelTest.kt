package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.DataResult
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Post
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.convertFromJsonToObjectList
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.getOrAwaitValue
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.getResourceAsText
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.rules.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PostsCoroutineViewModelTest {

    /**
     * Not using this causes java.lang.RuntimeException: Method getMainLooper in android.os.Looper
     * not mocked when <code>this.observeForever(observer)</code> is called
     */
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    private val useCase: PostsUseCase = mockk()
    private lateinit var viewModel: PostsCoroutineViewModel

    companion object {
        val postList =
            convertFromJsonToObjectList<Post>(getResourceAsText("posts.json"))!!
    }

    @Test
    fun `given DataResult Error returned from useCase, should result error`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            coEvery {
                useCase.getPosts()
            } returns DataResult.Error(Exception("Network error occurred."))

            // WHEN
            viewModel.getPostWithSuspend()

            // THEN
            val expected = viewModel.postStateWithSuspend.getOrAwaitValue()
            Truth.assertThat("Network error occurred.").isEqualTo(expected?.error?.message)
            Truth.assertThat(expected?.error).isInstanceOf(Exception::class.java)
            coVerify(atMost = 1) { useCase.getPosts() }
        }


    @Test
    fun `given DataResult Success returned from useCase, should return data`() {

        // GIVEN
        coEvery {
            useCase.getPosts()
        } returns DataResult.Success(postList)
        val actual = postList.first().title

        // WHEN
        viewModel.getPostWithSuspend()

        // THEN
        val expected = viewModel.postStateWithSuspend.getOrAwaitValue().data
        Truth.assertThat(expected).isEqualTo(actual)
        coVerify(atMost = 1) { useCase.getPosts() }
    }


    @Before
    fun setUp() {

        viewModel = PostsCoroutineViewModel(testCoroutineRule.testCoroutineScope, useCase)

        // provide the scope explicitly, in this example using a constructor parameter
        Dispatchers.setMain(testCoroutineRule.testCoroutineDispatcher)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}