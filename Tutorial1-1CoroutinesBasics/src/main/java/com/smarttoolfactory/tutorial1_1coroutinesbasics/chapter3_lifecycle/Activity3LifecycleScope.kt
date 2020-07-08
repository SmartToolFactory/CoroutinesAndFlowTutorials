package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter3_lifecycle

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.databinding.Activity3LifecycleScopeBinding
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.dataBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
*
* */

class Activity3LifecycleScope : AppCompatActivity(R.layout.activity3_lifecycle_scope) {

    private val binding: Activity3LifecycleScopeBinding by dataBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Mesajj")
        builder.setTitle("Titlee")
        val alertDialog = builder.create()


        binding.button1.setOnClickListener {
            binding.textViewResult.text = binding.textViewResult.text.toString() + "Clicked\n"
            lifecycleScope.launch {

                binding.textViewResult.text =
                    binding.textViewResult.text.toString() + "🤓 Delay 5 sn before showing dialog\n"
                delay(5000)

                alertDialog.show()

                binding.textViewResult.text =
                    binding.textViewResult.text.toString() + "🥳 Delay 5 sn after showing dialog\n"
                delay(5000)

                binding.textViewResult.text =
                    binding.textViewResult.text.toString() + "Dismissed AlertDialog \n"
                Toast.makeText(baseContext, "Dismissed AlertDialog", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
            binding.textViewResult.text =
                binding.textViewResult.text.toString() + "🚗🚗🚗 Codes after lifecycleScope \n"
        }

    }
}