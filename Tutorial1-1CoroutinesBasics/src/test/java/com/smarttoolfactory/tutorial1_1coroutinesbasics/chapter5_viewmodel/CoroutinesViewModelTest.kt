package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter5_viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutinesViewModelTest {

    /**
     * Not using this causes java.lang.RuntimeException: Method getMainLooper in android.os.Looper
     * not mocked when <code>this.observeForever(observer)</code> is called
     */
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: CoroutinesViewModel


    /**
     * * If this test is run WITHOUT advancing time it FAILS, time should progress more than
     * delay.
     *
     * * If this test is run in a scope different than that ViewModel(viewModelScope
     * or custom scope) runs it FAILS
     * TestCoroutineDispatcher or TestCoroutineScope can be used if ViewModel has [Dispatchers.Main]
     *
     */
    @Test
    fun `given timeout shorter than 2000 ms, should return mock response`() =
        testCoroutineDispatcher.runBlockingTest {

            // GIVEN
            val actual = "Hello World"

            // WHEN
            viewModel.getMockResult(1500)

            /*
                🔥🔥 Time should be progressed with advanceUntilIdle() or
                advanceTimeBy() with param that longer than delay
             */
            advanceUntilIdle()

            // THEN
            println("🤝 Test THEN")

            val expected = viewModel.result.getOrAwaitValue()
            Truth.assertThat(actual).isEqualTo(expected)

        }


    /**
     * If ViewModel does not use the same scope that this test uses,
     * it FAILS
     *
     * * This test is not working when testCoroutineDispatcher is used in launch
     * inside [CoroutinesViewModel.throwExceptionInAScope] function. This is a BUG in coroutines
     *  test
     */
    @Test(expected = RuntimeException::class)
    fun `Test function that throws exception`() =
        testCoroutineDispatcher.runBlockingTest {

            println("Test scope: $this, thread: ${Thread.currentThread().name}")
            /*
                🔥🔥🔥 Using testCoroutineDispatcher causes this test to FAIL
             */
            viewModel.throwExceptionInAScope(this.coroutineContext)

        }

    /**
     * ❌ Timeouts are NOT WORKING as of kotlinx-coroutines-test 1.3.6
     */
    @Test
    fun `Test timeout`() = testCoroutineDispatcher.runBlockingTest {

        // GIVEN
        val actual = "10"

        // WHEN
        viewModel.getMockResultWithTimeout()

        // THEN
        val expected = viewModel.resultWithTimeout.value
        Truth.assertThat(actual).isEqualTo(expected)
    }


    /**
     * ❌✅ This test PASSES but THROWS EXCEPTION 3 times ???
     */
    @Test
    fun `Test retry after 3 times`() = testCoroutineDispatcher.runBlockingTest {

        // GIVEN
        val actualPartial = "Exception Failed after 3 tries"

        // WHEN
        viewModel.getMockResultWithRetry()
        advanceUntilIdle()

        // THEN
        val expected = viewModel.resultWithRetry.value
        println("Test expected: $expected")
        Truth.assertThat(actualPartial).isEqualTo(expected)

    }

    /**
     * This test passes if time is advanced until every
     * internal coroutine is completed
     */
    @Test
    fun `Test concurrency of async coroutine builder`() = testCoroutineDispatcher.runBlockingTest {

        // GIVEN
        val actualPartial = "Hello World"

        // WHEN
        viewModel.getMultipleResponsesConcurrently()

        // 🔥 Advancing time is required, otherwise this test fails
        advanceTimeBy(3000)

        // THEN
        val expected = viewModel.resultMultiple.value
        println("Test expected: $expected")
        Truth.assertThat(expected?.contains(actualPartial)).isTrue()

    }

    @Before
    fun setUp() {

        viewModel = CoroutinesViewModel(testCoroutineDispatcher)

        // provide the scope explicitly, in this example using a constructor parameter
        Dispatchers.setMain(testCoroutineDispatcher)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        try {
            testCoroutineDispatcher.cleanupTestCoroutines()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}