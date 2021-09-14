package com.jameshill.dogsretrofit

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.jameshill.dogsretrofit.api.ApiRequest
import com.jameshill.dogsretrofit.api.BASE_URL
import com.jameshill.dogsretrofit.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Disables Night Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Activate Background Animation
        backgroundAnimation()

        //Load the first dog as soon as the app is opened.
        makeApiRequest()

        binding.floatingActionButton.setOnClickListener{

            binding.floatingActionButton.animate().apply{
                rotationBy(360f)
                duration = 1000
            }.start()

            makeApiRequest()
            binding.ivRandomDog.visibility = View.GONE
        }
    }

    private fun backgroundAnimation() {
        val animationDrawable: AnimationDrawable = binding.rlLayout.background as AnimationDrawable
            animationDrawable.apply {
            setEnterFadeDuration(1000)
            setExitFadeDuration(3000)
            start()
            }
    }

    private fun makeApiRequest() {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequest::class.java)

        GlobalScope.launch (Dispatchers.IO){
            try{
                val response = api.getRandomDog()
                Log.d("Main", "Size ${response.fileSizeBytes}")

                //If the image is less that about 0.4mb, we try to load it into the app.
                //else, try again.
                if (response.fileSizeBytes < 400000){
                    withContext(Dispatchers.Main){
                        Glide.with (applicationContext).load(response.url).into(binding.ivRandomDog)
                        binding.ivRandomDog.visibility = View.VISIBLE
                    }
                }else{
                    makeApiRequest()
                }
            } catch (e: Exception){
                Log.e("Main", "Error ${e.message}")
            }
        }
    }
}