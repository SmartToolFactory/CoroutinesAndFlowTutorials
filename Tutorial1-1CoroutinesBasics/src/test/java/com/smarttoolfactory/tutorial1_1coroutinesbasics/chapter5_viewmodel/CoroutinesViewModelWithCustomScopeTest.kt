package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter5_viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.getOrAwaitValue
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.rules.TestCoroutineRule
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutinesViewModelWithCustomScopeTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: CoroutinesViewModelWithCustomScope


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
        testCoroutineRule.runBlockingTest {

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
            println("🤬 Test THEN")

            val expected = viewModel.result.getOrAwaitValue()
            Truth.assertThat(actual).isEqualTo(expected)
        }

    /**
     * ❌✅ Test passes but THROWS exception either
     */
    @Test(expected = RuntimeException::class)
    fun `Test function that throws exception`() = testCoroutineRule.runBlockingTest {
        println("Test scope: $this, thread: ${Thread.currentThread().name}")
        viewModel.throwExceptionInAScope()

        // Advancing time not effecting this test
//            advanceUntilIdle()
    }

    /**
     * ❌ Timeouts are NOT WORKING as of kotlinx-coroutines-test 1.3.6
     */
    @Test
    fun `Test timeout`() = testCoroutineRule.runBlockingTest {

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
    fun `Test retry after 3 times`() = testCoroutineRule.runBlockingTest {

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
     * This test passes if time is advanced enough until every
     * internal coroutine is completed
     */
    @Test
    fun `Test concurrency of async coroutine builder`() = testCoroutineRule.runBlockingTest {

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
        viewModel = CoroutinesViewModelWithCustomScope(testCoroutineRule.testCoroutineScope)
    }

}