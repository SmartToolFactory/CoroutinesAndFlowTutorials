package com.smarttoolfactory.tutorial2_1flowbasics.chapter2_network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.smarttoolfactory.tutorial2_1flowbasics.TestCoroutineRule
import org.junit.Rule

class PostNetworkViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()


}