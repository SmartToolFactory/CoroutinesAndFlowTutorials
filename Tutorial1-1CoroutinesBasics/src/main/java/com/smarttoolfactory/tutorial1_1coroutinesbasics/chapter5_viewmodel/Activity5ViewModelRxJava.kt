package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter5_viewmodel

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smarttoolfactory.tutorial1_1basics.databinding.Activity5ViewmodelRxjavaBinding

class Activity5ViewModelRxJava : AppCompatActivity() {

    private val viewModel: RxJavaViewModel by viewModels()

    private val dataBinding by lazy {
        Activity5ViewmodelRxjavaBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dataBinding.root)

        dataBinding.apply {
            lifecycleOwner = this@Activity5ViewModelRxJava
            viewModel = this@Activity5ViewModelRxJava.viewModel
        }

    }

}