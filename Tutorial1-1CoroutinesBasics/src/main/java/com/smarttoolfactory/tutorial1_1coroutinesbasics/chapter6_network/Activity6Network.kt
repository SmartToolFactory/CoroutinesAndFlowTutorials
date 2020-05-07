package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smarttoolfactory.tutorial1_1basics.databinding.Activity6RetrofitBinding

class Activity6Network: AppCompatActivity() {

    private val viewModel: PostsCoroutineViewModel by viewModels()

    private val dataBinding by lazy {
        Activity6RetrofitBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dataBinding.root)

        dataBinding.apply {
            lifecycleOwner = this@Activity6Network
            dataBinding.viewModel = this@Activity6Network.viewModel
        }


    }

}