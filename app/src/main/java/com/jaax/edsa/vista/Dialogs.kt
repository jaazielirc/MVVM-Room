package com.jaax.edsa.vista

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.jaax.edsa.R

class SupportUser: DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.support_user, null)

        val builder = AlertDialog.Builder(activity)
        builder
            .setView(view)
            .setPositiveButton("Entendido") { _, _ -> dismiss() }
        return builder.create()
    }
}

class SupportNPss: DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.support_npss, null)

        val builder = AlertDialog.Builder(activity)
        builder
            .setView(view)
            .setPositiveButton("Entendido") { _, _ -> dismiss() }
        return builder.create()
    }
}