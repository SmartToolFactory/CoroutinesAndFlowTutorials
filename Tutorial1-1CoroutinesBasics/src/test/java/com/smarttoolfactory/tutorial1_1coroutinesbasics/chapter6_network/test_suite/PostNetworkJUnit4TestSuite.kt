package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.test_suite

import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.PostsCoroutineViewModelTest
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.PostsRepositoryTest
import org.junit.runner.RunWith
import org.junit.runners.Suite


// Runs all unit tests with JUnit4.
@RunWith(Suite::class)
@Suite.SuiteClasses(
    PostsRepositoryTest::class,
    PostsCoroutineViewModelTest::class
)
class PostNetworkJUnit4TestSuite
