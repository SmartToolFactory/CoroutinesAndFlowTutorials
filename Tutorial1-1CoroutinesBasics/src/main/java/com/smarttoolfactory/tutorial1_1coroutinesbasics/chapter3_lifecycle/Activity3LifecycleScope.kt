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
    lifecycleScope.launch{} is an alternatie to Handler().postDelayed()

    activity.lifecycleScope.launch {
     // scope bound to Activity Lifecycle
    }
    fragment.lifecycleScope.launch {
     // scope bound to Fragment Lifecycle
    }
    fragment.viewLifecycleOwner.launch{
     // scope bound to Fragment View
    }

    Be aware that lifecycleScope is convenient when dealing with UI events like, for example, showing a tip for the user and hiding it after a small delay.
    lifecycleScope.launch {
        delay(5000)
        showTip()
        delay(5000)
        hideTip()
    }

    Without using Coroutines and lifecycleScope, this would be:
    val DELAY = 5000
    Handler().postDelayed({
      showTip()
        Handler().postDelayed({
          hideTip()
        }, DELAY)
    }, DELAY)

    LifecycleScope is bound to Dispatcher.Main. That means if you don’t change Dispatcher explicitly all the coroutines inside LifecycleScope will be executed on the main thread.

    https://medium.com/corouteam/exploring-kotlin-coroutines-and-lifecycle-architectural-components-integration-on-android-c63bb8a9156f
    https://android.jlelse.eu/coroutine-in-android-working-with-lifecycle-fc9c1a31e5f3
    https://kotlin.christmas/2019/13
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