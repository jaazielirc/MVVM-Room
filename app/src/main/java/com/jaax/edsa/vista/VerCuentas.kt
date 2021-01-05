package com.jaax.edsa.vista

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jaax.edsa.R
import com.jaax.edsa.controlador.*
import com.jaax.edsa.modelo.Cuenta
import com.jaax.edsa.modelo.DBHelper
import java.sql.SQLException

class VerCuentas(private val ID: String): DialogFragment() {
    private lateinit var db: DBHelper
    private lateinit var addCuenta: FloatingActionButton
    private lateinit var listaCuentas: ListView
    private lateinit var txtNoAccount: TextView
    private lateinit var imgNoAccount: ImageView
    private lateinit var toast: Toast
    private lateinit var cuentaAdapter: CuentaAdapter

    @SuppressLint("ShowToast")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.show_accounts, null)
        val builder = AlertDialog.Builder(activity)

        addCuenta = view.findViewById(R.id.account_add)
        listaCuentas = view.findViewById(R.id.accounts_lista)
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
        mostrarCuentas()
    }

    override fun onResume() {
        super.onResume()
        onClick()
    }

    private fun onClick() {
        addCuenta.setOnClickListener {
            AddCuentaFragment(this.ID).show(
                this@VerCuentas.activity!!.supportFragmentManager, "addAccount"
            )
            this@VerCuentas.dismiss()
        }
        listaCuentas.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, view, pos, _ ->
            view?.isSelected = true
            val popupMenu = PopupMenu(this@VerCuentas.context, view)
            popupMenu.menuInflater.inflate(R.menu.opc_cuenta, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.menu_edacc -> {
                        val updt = UpdateCuentaFragment(
                            this.ID,
                            cuentaAdapter.cuentas[pos].usuario,
                            cuentaAdapter.cuentas[pos].passwrd,
                            cuentaAdapter.cuentas[pos].tipo
                        )
                        updt.show(this@VerCuentas.activity!!.supportFragmentManager, "updateCuenta")
                        this@VerCuentas.dismiss()
                    }
                    R.id.menu_delacc -> {
                        val del = DeleteCuentaFragment(
                            this.ID,
                            cuentaAdapter.cuentas[pos].usuario,
                            cuentaAdapter.cuentas[pos].tipo,
                        )
                        del.show(this@VerCuentas.activity!!.supportFragmentManager, "delCuenta")
                        this@VerCuentas.dismiss()
                    }
                }
                true
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                popupMenu.gravity = Gravity.CENTER_HORIZONTAL
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            }
            popupMenu.show()
            true
        }
    }

    private fun mostrarCuentas(){
        val allCuentas = ArrayList<Cuenta>()
        try {
            val cursor = db.getCuentasById(this.ID)
            if( cursor.count>0 ){
                while(cursor.moveToNext()){
                    val cuenta = Cuenta(
                        this.ID,
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                        )
                    allCuentas.add(cuenta)
                }
                txtNoAccount.visibility = View.GONE
                imgNoAccount.visibility = View.GONE
            }
            cuentaAdapter = CuentaAdapter(activity!!.applicationContext, allCuentas)
            listaCuentas.adapter = cuentaAdapter
        }catch (sql: SQLException){}
    }
    /*private fun refreshListAccounts(){
        val allCuentas = ArrayList<Cuenta>()
        try {
            val cursor = db.getCuentasById(this.ID)
            if( cursor.count>0 ){
                while(cursor.moveToNext()){
                    val cuenta = Cuenta(
                        this.ID,
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                    )
                    allCuentas.add(cuenta)
                }
                txtNoAccount.visibility = View.GONE
                imgNoAccount.visibility = View.GONE
            }
            cuentaAdapter.cuentas.clear()
            cuentaAdapter.cuentas.addAll(allCuentas)
            cuentaAdapter.notifyDataSetChanged()
        }catch (sql: SQLException){}
    }*/
}