package com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial2_1flowbasics.TestCoroutineRule
import com.smarttoolfactory.tutorial2_1flowbasics.convertFromJsonToObjectList
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Status
import com.smarttoolfactory.tutorial2_1flowbasics.getResourceAsText
import com.smarttoolfactory.tutorial2_1flowbasics.livedata.test
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostDBViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    private val useCase = mockk<PostDBUseCase>()

    private lateinit var postDBViewModel: PostDBViewModel

    private val postList by lazy {
        convertFromJsonToObjectList<Post>(getResourceAsText("response.json"))!!
    }

    @Test
    fun `given exception returned from useCase, should have ViewState with ERROR`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            every { useCase.getPostFlow() } returns flow<List<Post>> {
                emit(throw Exception("Network Exception"))
            }

            val testObserver = postDBViewModel.postViewState.test()

            // WHEN
            postDBViewModel.getPosts()

            // THEN
            testObserver
                .assertValues { states ->
                    (states[0].status == Status.LOADING &&
                            states[1].status == Status.ERROR)
                }
                .dispose()


            // THEN
            val finalState = testObserver.values()[1]
            Truth.assertThat("Network Exception").isEqualTo(finalState?.error?.message)
            Truth.assertThat(finalState?.error).isInstanceOf(Exception::class.java)
            verify(atMost = 1) { useCase.getPostFlow() }
        }

    @Test
    fun `given data retrieved from useCase, should have ViewState with SUCCESS and data`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            every { useCase.getPostFlow() } returns flow<List<Post>> {
                emit(postList)
            }
            val testObserver = postDBViewModel.postViewState.test()

            // WHEN
            postDBViewModel.getPosts()

            // THEN
            val viewStates = testObserver.values()
            Truth.assertThat(viewStates.first().status).isEqualTo(Status.LOADING)

            val actual = viewStates.last().data
            Truth.assertThat(actual?.size).isEqualTo(100)
            verify(atMost = 1) { useCase.getPostFlow() }
            testObserver.dispose()
        }


    @Before
    fun setUp() {
        postDBViewModel = PostDBViewModel(testCoroutineRule.testCoroutineScope, useCase)
    }

    @After
    fun tearDown() {
        clearMocks(useCase)
    }


}