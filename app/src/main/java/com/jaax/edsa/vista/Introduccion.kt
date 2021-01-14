package com.jaax.edsa.vista

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jaax.edsa.R
import java.lang.IllegalStateException

class Introduccion: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_base)
        gotoOpciones()
    }

    private fun gotoOpciones(){
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        val fragment: Fragment?
        try {
            manager.popBackStackImmediate()
            fragment = IntroOpcion1()
            transaction
                .replace(R.id.intro_base_for_fragments, fragment)
                .addToBackStack("intro1")
                .commit()
        } catch(ignored: IllegalStateException){
            ignored.printStackTrace()
        }
    }

    override fun onBackPressed() {}
}