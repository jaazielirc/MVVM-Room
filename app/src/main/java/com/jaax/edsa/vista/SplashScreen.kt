package com.jaax.edsa.vista

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaax.edsa.controlador.MainActivity
import com.jaax.edsa.R

class SplashScreen: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        val thread = object : Thread(){
            override fun run() {
                try {
                    sleep(3000)
                } finally {
                    val int = Intent(applicationContext, Introduccion::class.java)
                    startActivity(int)
                    finish()
                }
            }
        }
        thread.start()
    }
}