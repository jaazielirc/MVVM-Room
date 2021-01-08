package com.jaax.edsa.controlador

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.jaax.edsa.modelo.DBHelper
import com.jaax.edsa.modelo.Email
import com.jaax.edsa.R
import com.jaax.edsa.modelo.Usuario
import java.lang.ClassCastException
import java.sql.SQLException

class AddMailFragment(usuarioPadre: Usuario): DialogFragment(){
    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private lateinit var email: Email
    private lateinit var edTxtEmail: EditText
    private lateinit var edTxtPsswrd: EditText
    private lateinit var btnAgregar: Button
    private lateinit var callBack: OnCallbackReceivedAdd
    private var usuario: Usuario

    init {
        this.usuario = usuarioPadre
    }

    @SuppressLint("ShowToast")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.add_email, null)
        val builder = AlertDialog.Builder(activity)

        edTxtEmail = view.findViewById(R.id.addmail_nombre)
        edTxtPsswrd = view.findViewById(R.id.addmail_psswrd)
        btnAgregar = view.findViewById(R.id.addmail_btn)
        db = DBHelper(activity!!.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(activity!!.applicationContext, "txt", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        builder.setView(view)

        return builder.create()
    }

    interface OnCallbackReceivedAdd{
        fun refreshByAdding()
    }

    override fun onDestroy() {
        super.onDestroy()
        try{
            callBack = context as OnCallbackReceivedAdd
        }catch (cce: ClassCastException){cce.printStackTrace()}
        callBack.refreshByAdding()
    }

    override fun onResume() {
        super.onResume()
        btnAgregar.setOnClickListener {
            val edTextUser = edTxtEmail.text.toString()
            val edTextPass = edTxtPsswrd.text.toString()
            email = Email(usuario.nombre, edTextUser, edTextPass, ArrayList())
            val acceso = datosValidos(email.nombre, email.passwrd)

            if(acceso){
                val registro = agregarNuevoEmail(email)
                if(registro){
                    toast.setText("Email agregado")
                    toast.show()
                    dismiss()
                } else {
                    toast.setText("Error al agregar\nIntenta de nuevo")
                    toast.show()
                }
            }
        }
    }

    private fun agregarNuevoEmail(emailAddress: Email): Boolean{
        val cursor = db.getEmailsById(emailAddress.ID)
        val listaEmails = arrayListOf<String>()
        var i = 0
        var agregar = false
        try {
            if( cursor.count>0 ){
                while(cursor.moveToNext()){
                    listaEmails.add(cursor.getString(1))
                    i++
                }
                for(j: Int in 0 until listaEmails.size){
                    if( listaEmails[j] == emailAddress.nombre ){
                        i = 0
                        break
                    }
                }
                if(i != 0){ // i == 0 el email ya existía
                    agregar = db.insertarEmail(emailAddress.ID, emailAddress.nombre, emailAddress.passwrd)
                }
            } else {agregar = db.insertarEmail(emailAddress.ID, emailAddress.nombre, emailAddress.passwrd)} //primer email agregado

        } catch (ne: SQLException){}

        return agregar
    }

    private fun datosValidos(mail: String, pss: String): Boolean{
        val regexE = Regex(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,100}" + //letra o numero seguido de un caracter especial válido
                    "\\@" + //seguido de un @ (que es el limite del nombre del correo
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + //seguido de un dominio válido
                    "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})") //seguido de un punto con una extensión válida

        // [a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,100}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})

        val counts = arrayOf(0, 0)
        if( !mail.matches(regexE) ){
            edTxtEmail.error = "!"
            toast.setText("Ingresa una dirección de correo válida")
            toast.setGravity(Gravity.TOP, 0, -100)
            toast.show()
            counts[0]++
        }
        if( pss.length < 3 ){
            edTxtPsswrd.error = "!"
            toast.setText("Ingresa una contraseña válida")
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
            toast.show()
            counts[1]++
        }
        if( counts[0]==0 && counts[1]==0 )
            return true
        return false
    }
}