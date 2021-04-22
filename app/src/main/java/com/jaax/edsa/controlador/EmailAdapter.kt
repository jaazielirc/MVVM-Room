package com.jaax.edsa.controlador

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import com.jaax.edsa.modelo.Email
import com.jaax.edsa.R
import com.jaax.edsa.modelo.Usuario

class EmailAdapter(
    private val context: Context,
    private val manager: FragmentManager,
    private val usuario: Usuario,
    var emails: ArrayList<Email>
    ): BaseAdapter() {

    private class ViewHolder(view: View?){
        val nombreEmail = view?.findViewById(R.id.adapter_email) as TextView
        val password = view?.findViewById(R.id.adapter_psswrd) as TextView
        val viewAccounts = view?.findViewById( R.id.imgBtnViewAccounts ) as ImageButton
        val edit = view?.findViewById( R.id.imgBtnEdit ) as ImageButton
        val delete = view?.findViewById( R.id.imgBtnDelete ) as ImageButton
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder

        if( convertView == null ){
            val layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.datos_adapter_email, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val email: Email = getItem(position) as Email
        viewHolder.nombreEmail.text = email.nombre
        viewHolder.password.text = email.passwrd

        viewHolder.viewAccounts.setOnClickListener {
            VerCuentas(email).show(manager, "verCuentas")
        }
        viewHolder.edit.setOnClickListener {
            val updt = UpdateMailFragment(email, usuario)
            val bundle = Bundle()
            val bundleNombre = email.nombre
            val bundlePsswrd = email.passwrd //asignar por medio de la BD
            updt.arguments = bundle
            bundle.putString("bundleEmailNombre", bundleNombre)
            bundle.putString("bundleEmailPsswrd", bundlePsswrd)
            updt.show(manager, "updateEmail")
        }
        viewHolder.delete.setOnClickListener {
            DeleteMailFragment(
                email,
                usuario
            ).show(manager, "deleteEmail")
        }
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

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(buscar: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                if( TextUtils.isEmpty(buscar) ){
                    filterResults.count = emails.size
                    filterResults.values = emails
                } else {
                    val emailsFound = ArrayList<Email>()
                    for( ef: Email in emails){
                        if( ef.nombre.contains(buscar!!, true) ){
                            emailsFound.add( ef )
                        }
                    }
                    filterResults.count = emailsFound.size
                    filterResults.values = emailsFound
                }
                return filterResults
            }

            override fun publishResults(buscar: CharSequence?, results: FilterResults?) {
                if( results!!.count == 0 ){
                    notifyDataSetInvalidated()
                } else {
                    emails = results.values as ArrayList<Email>
                    notifyDataSetChanged()
                }
            }
        }
    }
}