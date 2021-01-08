package com.jaax.edsa.controlador

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.jaax.edsa.R
import com.jaax.edsa.modelo.Cuenta
import com.jaax.edsa.modelo.DBHelper
import com.jaax.edsa.modelo.Email

class AddCuentaFragment(emailPadre: Email): DialogFragment() {
    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private lateinit var cuenta: Cuenta
    private lateinit var edTxtUser: EditText
    private lateinit var edTxtPsswrd: EditText
    private lateinit var edTxtTipo: EditText
    private lateinit var btnAgregar: Button
    private var email: Email

    init {
        this.email = emailPadre
    }

    @SuppressLint("ShowToast")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.add_account, null)
        val builder = AlertDialog.Builder(activity)

        edTxtUser = view.findViewById(R.id.addacc_usuario)
        edTxtPsswrd = view.findViewById(R.id.addacc_psswrd)
        edTxtTipo = view.findViewById(R.id.addacc_tipo)
        btnAgregar = view.findViewById(R.id.addacc_btn)
        db = DBHelper(activity!!.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(activity!!.applicationContext, "txt", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        edTxtUser.setText(email.nombre)
        builder.setView(view)

        return builder.create()
    }

    override fun onResume() {
        super.onResume()
        btnAgregar.setOnClickListener {
            val user = edTxtUser.text.toString()
            val pass = edTxtPsswrd.text.toString()
            val type = edTxtTipo.text.toString()
            cuenta = Cuenta(email.nombre, user, pass, type)
            val acceso = datosValidos(cuenta.usuario, cuenta.tipo)

            if( acceso ){
                val registro = agregarNuevaCuenta(cuenta)
                if( registro ){
                    if(registro){
                        toast.setText("Cuenta agregada")
                        toast.show()
                        dismiss()
                    } else {
                        toast.setText("Error al agregar\nIntenta de nuevo")
                        toast.show()
                    }
                }
            }
        }
    }

    private fun agregarNuevaCuenta(newCuenta: Cuenta): Boolean{
        val cursor = db.getCuentasById(newCuenta.ID)
        val listaCuentas = arrayListOf<String>()
        var i = 0
        var agregar = false
        try {
            if( cursor.count>0 ){
                while(cursor.moveToNext()){
                    listaCuentas.add(cursor.getString(3)) // sólo nos interesa el tipo por si ya existe
                    i++
                }
                for(j: Int in 0 until listaCuentas.size){
                    if( listaCuentas[j] == newCuenta.tipo ){
                        i = 0
                        break
                    }
                }
                if(i != 0){
                    agregar = db.insertarCuenta(cuenta.ID, cuenta.usuario, cuenta.passwrd, cuenta.tipo)
                }
            } else {agregar = db.insertarCuenta(cuenta.ID, cuenta.usuario, cuenta.passwrd, cuenta.tipo)} //primer cuenta agregada

        } catch (ne: SQLiteException){ne.printStackTrace()}

        return agregar
    }

    private fun datosValidos(usr: String, type: String): Boolean{
        val counts = arrayOf(0, 0)
        if(usr.isEmpty()){
            edTxtUser.error = "!"
            toast.setText("Ingresa un usuario válido")
            toast.setGravity(Gravity.TOP, 0, -100)
            toast.show()
            counts[0]++
        }
        if( type.length < 2 ){
            edTxtTipo.error = "!"
            toast.setText("Ingresa un tipo válido (YT, FB, Twitter, etc)")
            toast.setGravity(Gravity.BOTTOM, 0, 0)
            toast.show()
            counts[2]++
        }
        if(counts[0]==0 && counts[1]==0 )
            return true
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        VerCuentas(email).show(
            this@AddCuentaFragment.activity!!.supportFragmentManager, "verCuentas"
        )
    }
}