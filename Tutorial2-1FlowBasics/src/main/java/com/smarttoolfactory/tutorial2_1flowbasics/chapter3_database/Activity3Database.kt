package com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.smarttoolfactory.tutorial2_1flowbasics.R
import com.smarttoolfactory.tutorial2_1flowbasics.adapter.PostListAdapter
import com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database.mock.MockProductFactory
import com.smarttoolfactory.tutorial2_1flowbasics.databinding.Activity3DatabaseBinding
import com.smarttoolfactory.tutorial2_1flowbasics.di.ServiceLocator

class Activity3Database : AppCompatActivity() {

    lateinit var dataBinding: Activity3DatabaseBinding

    private val viewModel: PostDBViewModel by viewModels {
        PostViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity3_database)


        viewModel.postCount.observe(this, Observer {

            val postCount = it.getContentIfNotHandled()
            Toast.makeText(this, "Post count: $postCount", Toast.LENGTH_SHORT)
                .show()

            if (postCount == 0) {
                saveMockPosts()
            }
        })

        viewModel.getPostCount()

        viewModel.getPosts()
        bindViews()
    }

    private fun saveMockPosts() {
        val serviceLocator = ServiceLocator(application)
        val mockProductFactory = MockProductFactory(serviceLocator.providePostDao(), application)
        viewModel.savePosts(mockProductFactory.generateMockList())
    }

    private fun bindViews() {

        val binding = dataBinding!!

        // 🔥 Set lifecycle for data binding
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerView.apply {

            // Set Layout manager
            this.layoutManager =
                LinearLayoutManager(this@Activity3Database, LinearLayoutManager.VERTICAL, false)

            // Set RecyclerViewAdapter
            this.adapter =
                PostListAdapter(
                    R.layout.row_post,
                    viewModel::onClick
                )
        }
    }
}