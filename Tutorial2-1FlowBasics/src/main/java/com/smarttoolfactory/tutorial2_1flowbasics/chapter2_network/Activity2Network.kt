package com.smarttoolfactory.tutorial2_1flowbasics.chapter2_network

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.smarttoolfactory.tutorial2_1flowbasics.R
import com.smarttoolfactory.tutorial2_1flowbasics.adapter.PostListAdapter
import com.smarttoolfactory.tutorial2_1flowbasics.databinding.Activity2NetworkBinding

class Activity2Network : AppCompatActivity() {

    lateinit var dataBinding: Activity2NetworkBinding

    private val viewModel: PostNetworkViewModel by viewModels {
        PostViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity2_network)

        viewModel.getPosts()
        bindViews()
    }

    private fun bindViews() {

        val binding = dataBinding!!

        // 🔥 Set lifecycle for data binding
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerView.apply {

            // Set Layout manager
            this.layoutManager =
                LinearLayoutManager(this@Activity2Network, LinearLayoutManager.VERTICAL, false)

            // Set RecyclerViewAdapter
            this.adapter =
                PostListAdapter(
                    R.layout.row_post,
                    viewModel::onClick
                )
        }
    }
}