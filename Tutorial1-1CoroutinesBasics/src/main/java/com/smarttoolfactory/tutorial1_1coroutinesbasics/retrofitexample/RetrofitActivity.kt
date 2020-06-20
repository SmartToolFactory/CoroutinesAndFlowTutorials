package com.smarttoolfactory.tutorial1_1coroutinesbasics.retrofitexample

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smarttoolfactory.tutorial1_1basics.R
import kotlinx.android.synthetic.main.activity_retrofit.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** example taken from https://github.com/coding-blocks-archives/Retrofit-Coroutines-Sample-Android */
class RetrofitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)

        val r = Retrofit.Builder()
            .baseUrl("https://reqres.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = r.create(ReqResAPI::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val res1 = async { api.getUsers(1) }
                Log.d(TAG, "first page done")
                val res2 = async { api.getUsers(2) }
                Log.d(TAG, "second page done")

                val res = awaitAll(res1, res2)
                Log.d(
                    TAG,
                    "both pages done. ${res[0].body()?.users?.size} users retrieved from first url. ${res[1].body()?.users?.size} users retrieved from second url."
                )

                val users = ArrayList<UsersResponse.User>()
                res[0].body()?.users?.let { users.addAll(it) }
                res[1].body()?.users?.let { users.addAll(it) }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RetrofitActivity, "Request Success", Toast.LENGTH_SHORT)
                        .show()

                    printUsers(users)
                }


            } catch (e: Exception) {
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(this@RetrofitActivity, "Request Success", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun printUsers(users: List<UsersResponse.User>) {
        lvPeople.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            users.map { it.firstName }
        )
    }

    companion object {
        const val TAG = "RetrofitActivityExample"
    }
}