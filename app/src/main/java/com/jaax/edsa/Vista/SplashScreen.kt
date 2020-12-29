package com.jaax.edsa.Vista

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.jaax.edsa.MainActivity

class SplashScreen: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val thread = object : Thread(){
            override fun run() {
                try {
                    Thread.sleep(3000)
                } finally {
                    val int = Intent(applicationContext, MainActivity::class.java)
                    startActivity(int)
                }
            }
        }
        thread.start()
    }
}