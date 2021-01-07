package com.jaax.edsa.vista

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.jaax.edsa.R
import com.jaax.edsa.controlador.MainActivity

class IntroOpcion1: Fragment() {
    private lateinit var btnTutorial: Button
    private lateinit var btnRegistro: Button
    private lateinit var transaction: FragmentTransaction
    private lateinit var fragment: Fragment
    private lateinit var manager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.intro_1_opciones, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        manager = activity!!.supportFragmentManager
        btnTutorial = activity!!.findViewById(R.id.intro_goto_tutorial)
        btnRegistro = activity!!.findViewById(R.id.intro_goto_registro)
    }

    override fun onResume() {
        super.onResume()
        btnTutorial.setOnClickListener { gotoNextFragment( true ) }
        btnRegistro.setOnClickListener { gotoNextFragment( false ) }
    }

    private fun gotoNextFragment( go: Boolean ){
        if( go ){
            transaction = manager.beginTransaction()
            fragment = IntroOpcion2()

            manager.popBackStack("intro1", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            transaction
                .replace(R.id.intro_base_for_fragments, fragment)
                .addToBackStack("intro2")
                .commit()
        } else {
            val intent = Intent(activity!!.applicationContext, MainActivity::class.java)
            startActivity(intent)
            manager.popBackStack("intro1", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            activity!!.finish()
        }
    }
}