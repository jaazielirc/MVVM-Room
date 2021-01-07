package com.jaax.edsa.vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.jaax.edsa.R

class IntroOpcion5: Fragment() {
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var transaction: FragmentTransaction
    private lateinit var fragment: Fragment
    private lateinit var manager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.intro_5_funcion_c, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        manager = activity!!.supportFragmentManager
        btnPrevious = activity!!.findViewById(R.id.funcionc_previous)
        btnNext = activity!!.findViewById(R.id.funcionc_next)
    }

    override fun onResume() {
        super.onResume()
        btnPrevious.setOnClickListener { gotoNextFragment( false ) }
        btnNext.setOnClickListener { gotoNextFragment( true ) }
    }

    private fun gotoNextFragment( go: Boolean ){
        if( go ){
            transaction = manager.beginTransaction()
            fragment = IntroOpcion6()

            manager.popBackStack("intro5", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            transaction
                .replace(R.id.intro_base_for_fragments, fragment)
                .addToBackStack("intro6")
                .commit()
        } else {
            transaction = manager.beginTransaction()
            fragment = IntroOpcion4()

            manager.popBackStack("intro5", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            transaction
                .replace(R.id.intro_base_for_fragments, fragment)
                .addToBackStack("intro4")
                .commit()
        }
    }
}