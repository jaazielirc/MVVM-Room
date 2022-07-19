package com.jaax.edsa

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jaax.edsa.data.model.Account
import com.jaax.edsa.data.model.Email
import java.sql.SQLException

class VerCuentas(emailElegido: Email): DialogFragment(), AccountAdapter.DismissListener {

    private lateinit var db: DBHelper
    private lateinit var addCuenta: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var listaCuentas: ListView
    private lateinit var txtNoAccount: TextView
    private lateinit var imgNoAccount: ImageView
    private lateinit var toast: Toast
    private lateinit var adview3: AdView
    private lateinit var accountAdapter: AccountAdapter
    private var emailActual: Email

    init {
        emailActual = emailElegido
    }

    @SuppressLint("ShowToast")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.show_accounts, null)
        val builder = AlertDialog.Builder(activity)

        addCuenta = view.findViewById(R.id.account_add)
        listaCuentas = view.findViewById(R.id.accounts_lista)
        toolbar = view.findViewById(R.id.account_toolbar)
        txtNoAccount = view.findViewById(R.id.txtNoAccount)
        imgNoAccount = view.findViewById(R.id.imgNoAccount)
        db = DBHelper(activity!!.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(activity!!.applicationContext, "txt", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        builder.setView(view)

        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        toolbar.title = emailActual.nombre
        mostrarCuentas()
        MobileAds.initialize( activity!!.applicationContext )

        val adRequest = AdRequest.Builder().build()
        adview3.loadAd( adRequest )
    }

    override fun onResume() {
        super.onResume()
        onClick()
    }

    private fun onClick() {
        addCuenta.setOnClickListener {
            AddCuentaFragment(emailActual).show(
                this@VerCuentas.activity!!.supportFragmentManager, "addAccount"
            )
            this@VerCuentas.dismiss()
        }
    }

    private fun mostrarCuentas(){
        val allAccounts = ArrayList<Account>()
        try {
            val cursor = db.getCuentasById(emailActual.nombre)
            if( cursor.count>0 ){
                while(cursor.moveToNext()){
                    val account = Account(
                        emailActual.nombre,
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                        )
                    allAccounts.add(account)
                }
                emailActual = setMissingDataCuenta(emailActual)
                txtNoAccount.visibility = View.GONE
                imgNoAccount.visibility = View.GONE
            }
            accountAdapter = AccountAdapter(activity!!.applicationContext, activity!!.supportFragmentManager, allAccounts)
            listaCuentas.adapter = accountAdapter
        }catch (sql: SQLException){}
    }

    private fun setMissingDataCuenta( currentEmail: Email): Email {
        val cursor = db.getCuentasById(currentEmail.nombre)
        val listaAccounts = ArrayList<Account>()

        try { //no agrego 'if' xq si no tiene cuentas, entonces no hay nada que cliquear
            while( cursor.moveToNext() ) {
                val account = Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
                listaAccounts.add(account)
                currentEmail.accounts = listaAccounts
            }
        } catch(sqli: SQLiteException){ sqli.toString() }
        return currentEmail
    }

    override fun dismissFragment( received: Boolean ) {
        if( received ){
            this.dismiss()
        }
    }
}