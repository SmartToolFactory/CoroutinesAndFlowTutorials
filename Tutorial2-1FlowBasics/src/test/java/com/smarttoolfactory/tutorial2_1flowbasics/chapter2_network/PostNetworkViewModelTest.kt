package com.smarttoolfactory.tutorial2_1flowbasics.chapter2_network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial2_1flowbasics.TestCoroutineRule
import com.smarttoolfactory.tutorial2_1flowbasics.convertFromJsonToObjectList
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Status
import com.smarttoolfactory.tutorial2_1flowbasics.getResourceAsText
import com.smarttoolfactory.tutorial2_1flowbasics.livedata.getOrAwaitValue
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

class PostNetworkViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    private val useCase = mockk<PostRemoteUseCase>()

    private lateinit var postNetworkViewModel: PostNetworkViewModel

    private val postList by lazy {
        convertFromJsonToObjectList<Post>(getResourceAsText("response.json"))!!
    }


    /**
     * Test for testing LiveData util [TestObserver]
     */
    @Test
    fun test() {

        // GIVEN
        val myTestData = MutableLiveData<Int>()
        val testObserver = myTestData.test()

        // WHEN
//        myTestData.value = 1
//        myTestData.value = 2
//        myTestData.value = 3

        // THEN
        myTestData.getOrAwaitValue()
//        testObserver
//            .assertValues { list ->
//                (list[0] == 1 && list[1] == 2 && list[2] == 3)
//            }
//            .assertValueCount(3)
//            .dispose()

        // 🔥 Do not forget to dispose
//        testObserver.dispose()
    }

    @Test
    fun `given exception returned from useCase, should have ViewState with ERROR`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            every { useCase.getPostFlow() } returns flow<List<Post>> {
                emit(throw Exception("Network Exception"))
            }
            val testObserver = postNetworkViewModel.postViewState.test()

            // WHEN
            postNetworkViewModel.getPosts()

            // THEN
            testObserver
                .assertValues { states ->

                    (states[0].status == Status.LOADING &&
                            states[1].status == Status.ERROR)
                }


            // THEN
            val finalState = testObserver.values()[1]

            Truth.assertThat("Network Exception").isEqualTo(finalState?.error?.message)
            Truth.assertThat(finalState?.error).isInstanceOf(Exception::class.java)
            verify(atMost = 1) { useCase.getPostFlow() }
            testObserver.dispose()
        }

    @Test
    fun `given data retrieved from useCase, should have ViewState with SUCCESS and data`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN

            // WHEN

            // THEN

        }

    @Before
    fun setUp() {
        postNetworkViewModel = PostNetworkViewModel(testCoroutineRule.testCoroutineScope, useCase)
    }

    @After
    fun tearDown() {
        clearMocks(useCase)
    }

}