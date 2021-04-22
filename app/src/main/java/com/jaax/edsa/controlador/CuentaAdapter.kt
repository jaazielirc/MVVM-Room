package com.jaax.edsa.controlador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.jaax.edsa.R
import com.jaax.edsa.modelo.Cuenta
import java.lang.ClassCastException

class CuentaAdapter(
    private val context: Context,
    private val manager: FragmentManager,
    private var cuentas: ArrayList<Cuenta>
    ): BaseAdapter() {

    private var callback: DismissListener? = null

    init {
        try {
            callback = context as? DismissListener
        } catch(cce: ClassCastException) {
            cce.printStackTrace()
        }
    }

    interface DismissListener {
        fun dismissFragment( received: Boolean )
    }

    private class ViewHolder(view: View?){
        val password = view?.findViewById(R.id.adapter_pass) as TextView
        val tipo = view?.findViewById(R.id.adapter_tipo) as TextView
        val edit = view?.findViewById( R.id.imgBtnEdit ) as ImageButton
        val delete = view?.findViewById( R.id.imgBtnDelete ) as ImageButton
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
        viewHolder.password.text = cuenta.passwrd
        viewHolder.tipo.text = cuenta.tipo

        viewHolder.edit.setOnClickListener {
            callback?.dismissFragment( true )
            manager.popBackStack()
            val updt = UpdateCuentaFragment(cuenta)
            updt.show(manager, "updateCuenta")
        }
        viewHolder.delete.setOnClickListener {
            callback?.dismissFragment( true )
            manager.popBackStack()
            val del = DeleteCuentaFragment(cuenta)
            del.show(manager, "delCuenta")
        }
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