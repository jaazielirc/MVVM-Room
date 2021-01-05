package com.jaax.edsa.controlador

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.TextView
import com.jaax.edsa.modelo.Email
import com.jaax.edsa.R

class EmailAdapter(private val context: Context, var emails: ArrayList<Email>): BaseAdapter() {
    private class ViewHolder(view: View?){
        var nombreEmail: TextView
        var password: TextView

        init {
            this.nombreEmail = view?.findViewById(R.id.adapter_email) as TextView
            this.password = view.findViewById(R.id.adapter_psswrd) as TextView
        }
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
        val filter = object : Filter() {
            override fun performFiltering(buscar: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                if( TextUtils.isEmpty(buscar) ){
                    filterResults.count = emails.size
                    filterResults.values = emails
                } else {
                    val emailsFound = ArrayList<Email>()
                    for( ef: Email in emails ){
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
        return filter
    }
}