package com.jaax.edsa.controlador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.jaax.edsa.R
import com.jaax.edsa.modelo.Cuenta

class CuentaAdapter(private val context: Context, var cuentas: ArrayList<Cuenta>): BaseAdapter() {
    private class ViewHolder(view: View?){
        var nombreUser: TextView
        var password: TextView
        var tipo: TextView

        init {
            this.nombreUser = view?.findViewById(R.id.adapter_user) as TextView
            this.password = view.findViewById(R.id.adapter_pass) as TextView
            this.tipo = view.findViewById(R.id.adapter_tipo) as TextView
        }
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder

        if( convertView == null ){
            val layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.datos_adapter_cuenta, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val cuenta: Cuenta = getItem(position) as Cuenta
        viewHolder.nombreUser.text = cuenta.usuario
        viewHolder.password.text = cuenta.passwrd
        viewHolder.tipo.text = cuenta.tipo

        return view!!
    }
    override fun getCount(): Int {
        return cuentas.count()
    }

    override fun getItem(p0: Int): Any {
        return cuentas[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
}