package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.tutorial1_1basics.databinding.Activity7DatabaseBinding

class Activity7_1Database : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MeasurementViewModelFactory(application)
        ).get(MeasurementViewModel::class.java)
    }

    private val dataBinding by lazy {
        Activity7DatabaseBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dataBinding.root)

        dataBinding.apply {
            lifecycleOwner = this@Activity7_1Database
            dataBinding.viewModel = this@Activity7_1Database.viewModel
        }
    }
}