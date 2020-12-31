package com.jaax.edsa.Vista

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
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
            .setPositiveButton("Entendido", object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    dismiss()
                }
            })
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
            .setPositiveButton("Entendido", object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    dismiss()
                }
            })
        return builder.create()
    }
}