package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter5_viewmodel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.tutorial1_1basics.databinding.Activity5ViewmodelCoroutinesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class Activity5ViewModelScope : AppCompatActivity() {


//    private val viewModel: CoroutinesViewModel by viewModels()
    /**
     * CoroutineScope is required to run tests
     */
    private val viewModel by lazy {

        ViewModelProvider(
            this,
            CoroutinesViewModelFactory()
        ).get(CoroutinesViewModel::class.java)
    }

    private val dataBinding by lazy {
        Activity5ViewmodelCoroutinesBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dataBinding.root)

        dataBinding.apply {
            lifecycleOwner = this@Activity5ViewModelScope
            viewModel = this@Activity5ViewModelScope.viewModel
        }


    }

}