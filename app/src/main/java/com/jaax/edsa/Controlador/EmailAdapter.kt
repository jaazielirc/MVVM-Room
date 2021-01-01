package com.jaax.edsa.Controlador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.ToggleButton
import com.jaax.edsa.Modelo.Email
import com.jaax.edsa.R

class EmailAdapter(val context: Context, val emails: ArrayList<Email>): BaseAdapter() {

    private class ViewHolder(view: View?){
        var nombreEmail: TextView
        var password: TextView
        var toggleBtn: ToggleButton

        init {
            this.nombreEmail = view?.findViewById(R.id.adapter_email) as TextView
            this.password = view.findViewById(R.id.adapter_psswrd) as TextView
            this.toggleBtn = view.findViewById(R.id.adapter_toggle) as ToggleButton
        }
    }
    fun getData(): ArrayList<Email> {
        return emails
    }
    override fun getView(i: Int, convertView: View?, viewg: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder

        if( convertView == null ){
            val layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.datos_adapter_email, viewg, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val email: Email = getItem(i) as Email
        viewHolder.nombreEmail.text = email.nombre
        viewHolder.password.text = email.passwrd

        return view!!
    }
    override fun getCount(): Int {
        return emails.count()
    }

    override fun getItem(p0: Int): Any {
        return emails[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
}