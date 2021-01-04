package com.jaax.edsa.Vista

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.DialogFragment
import com.jaax.edsa.Modelo.DBHelper
import com.jaax.edsa.Modelo.Email
import com.jaax.edsa.Modelo.Usuario
import com.jaax.edsa.R
import java.sql.SQLException

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

class AddMailFragment(val ID: String): DialogFragment(){
    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private lateinit var email: Email
    private lateinit var edTxtEmail: EditText
    private lateinit var edTxtPsswrd: EditText
    private lateinit var btnAgregar: Button

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

    override fun onResume() {
        super.onResume()
        btnAgregar.setOnClickListener {
            val edTextUser = edTxtEmail.text.toString()
            val edTextPass = edTxtPsswrd.text.toString()
            email = Email(ID, edTextUser, edTextPass, ArrayList())
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
                    /*btnAgregar.isEnabled = false
                    btnAgregar.setBackgroundResource(R.drawable.disable_btn_style)
                    btnAgregar.setTextColor(Color.GRAY)
                    val thread = object : Thread() {
                        override fun run() {
                            try {
                                sleep(2000)
                            } finally {
                                btnAgregar.isEnabled = true
                                btnAgregar.setBackgroundResource(R.drawable.btn_style_dk)
                                btnAgregar.setTextColor(Color.BLACK)
                            }
                        }
                    }
                    thread.start()*/
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

            /*activity!!.runOnUiThread(object : Thread(){
                override fun run() {
                    try {
                        btnAgregar.isEnabled = false
                        btnAgregar.setBackgroundResource(R.drawable.disable_btn_style)
                        btnAgregar.setTextColor(Color.GRAY)
                        sleep(2000)
                    } finally {
                        btnAgregar.isEnabled = true
                        btnAgregar.setBackgroundResource(R.drawable.btn_style_dk)
                        btnAgregar.setTextColor(Color.BLACK)
                    }
                }
            })*/
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

class UpdateMailFragment(val ID: String): DialogFragment(){
    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private lateinit var email: Email
    private lateinit var usuario: Usuario
    private lateinit var edTxtEmail: EditText
    private lateinit var edTxtPsswrd: EditText
    private lateinit var edTxtKeyword: EditText
    private lateinit var btnModificar: Button
    private lateinit var toggleNombre: ToggleButton
    private lateinit var togglePsswrd: ToggleButton
    private lateinit var bundleN: String
    private lateinit var bundleP: String

    @SuppressLint("ShowToast")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.edit_email, null)
        val builder = AlertDialog.Builder(activity)

        edTxtEmail = view.findViewById(R.id.edmail_nombre)
        edTxtPsswrd = view.findViewById(R.id.edmail_psswrd)
        edTxtKeyword = view.findViewById(R.id.edmail_keyword)
        btnModificar = view.findViewById(R.id.edmail_btn)
        toggleNombre = view.findViewById(R.id.edmail_toggle_nombre)
        togglePsswrd = view.findViewById(R.id.edmail_toggle_psswrd)
        db = DBHelper(activity!!.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(activity!!.applicationContext, "txt", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        builder.setView(view)
        initDatos()
        return builder.create()
    }

    override fun onResume() {
        super.onResume()
        onClick()
    }

    private fun initDatos() {
        if(arguments != null){
            bundleN = arguments?.getString("bundleEmailNombre")!!
            bundleP = arguments?.getString("bundleEmailPsswrd")!!
            edTxtEmail.setText( bundleN )
            edTxtPsswrd.setText( bundleP )

            email = Email(this.ID, bundleN, bundleP, ArrayList())
            usuario = getDatosUsuario(email.ID)
        }
    }

    private fun onClick() {
        btnModificar.setOnClickListener {
            val edTextUser = edTxtEmail.text.toString()
            val edTextPass = edTxtPsswrd.text.toString()
            val edTextKey = edTxtKeyword.text.toString()
            val newEmail = Email( usuario.nombre, edTextUser, edTextPass, ArrayList() )

            Log.i("OrigiEmail", "${email.ID} - ${email.nombre} - ${email.passwrd}")
            Log.i("ModifEmail", "${newEmail.ID} - ${newEmail.nombre} - ${newEmail.passwrd}")

            val acceso = datosValidos(newEmail.nombre, newEmail.passwrd, usuario.keyword, edTextKey)

            if(acceso){
                val edicion = modificarEmail(email, newEmail)
                if(edicion){
                    toast.setText("Email actualizado")
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0 ,0)
                    toast.show()
                    dismiss()
                } else {
                    if( (email.nombre==newEmail.nombre) && (email.passwrd==newEmail.passwrd) ){
                        toast.setText("No hay cambios a realizar")
                        toast.setGravity(Gravity.TOP, 0 ,0)
                        toast.show()
                    } else {
                        toast.setText("Error al actualizar\nIntenta de nuevo")
                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0 ,0)
                        toast.show()
                    }
                    /*btnAgregar.isEnabled = false
                    btnAgregar.setBackgroundResource(R.drawable.disable_btn_style)
                    btnAgregar.setTextColor(Color.GRAY)
                    val thread = object : Thread() {
                        override fun run() {
                            try {
                                sleep(2000)
                            } finally {
                                btnAgregar.isEnabled = true
                                btnAgregar.setBackgroundResource(R.drawable.btn_style_dk)
                                btnAgregar.setTextColor(Color.BLACK)
                            }
                        }
                    }
                    thread.start()*/
                }
            }
        }
        toggleNombre.setOnCheckedChangeListener { _, isChecked ->
            if( isChecked  ){
                edTxtEmail.isEnabled = true
                edTxtEmail.requestFocus()
            } else {
                edTxtEmail.isEnabled = false
            }
        }
        togglePsswrd.setOnCheckedChangeListener { _, isChecked ->
            if( isChecked  ){
                edTxtPsswrd.isEnabled = true
                edTxtPsswrd.requestFocus()
            } else {
                edTxtPsswrd.isEnabled = false
            }
        }
    }

    private fun getDatosUsuario(iduser: String): Usuario {
        val cursor = db.getUsuarioById(iduser)
        val usr = Usuario("?", "?", "?", ArrayList())
        try {
            if( cursor.count>0 ){ //si existe el usuario
                while( cursor.moveToNext() ){
                    usr.nombre = cursor.getString(0)
                    usr.password = cursor.getString(1)
                    usr.keyword = cursor.getString(2)
                }
                return usr
            }
        }catch (exc: SQLiteException){}
        return usr
    }

    private fun modificarEmail(emailAddress: Email, newEmailAddrss: Email): Boolean{
        val cursor = db.getDatosEmailById(emailAddress.ID, emailAddress.nombre) //debe haber sólo 1 email si existe
        var actualizar = false
        try {
            if( cursor.count>0 ){
                if( (emailAddress.nombre==newEmailAddrss.nombre) && (emailAddress.passwrd==newEmailAddrss.passwrd) ){
                    toast.setText("Sin cambios2")
                    toast.setGravity(Gravity.BOTTOM, 0 ,0)
                    toast.show()
                    return actualizar
                } else {actualizar = db.updtDatosEmail( emailAddress.ID, emailAddress.nombre, newEmailAddrss.nombre, newEmailAddrss.passwrd )}
            } else {
                toast.setText("No existe ese email")
                toast.show()
            }
        } catch (ne: SQLException){ne.printStackTrace()}

        return actualizar
    }

    private fun datosValidos(mail: String, pss: String, key: String, matchKey: String): Boolean{
        val regexE = Regex(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,100}" + //letra o numero seguido de un caracter especial válido
                    "\\@" + //seguido de un @ (que es el limite del nombre del correo
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + //seguido de un dominio válido
                    "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})") //seguido de un punto con una extensión válida

        // [a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,100}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})

        val counts = arrayOf(0, 0, 0)
        if( !mail.matches(regexE) ){
            edTxtEmail.error = "!"
            toast.setText("Ingresa una dirección de correo válida")
            toast.setGravity(Gravity.TOP, 0, -100)
            toast.show()

            /*activity!!.runOnUiThread(object : Thread(){
                override fun run() {
                    try {
                        btnAgregar.isEnabled = false
                        btnAgregar.setBackgroundResource(R.drawable.disable_btn_style)
                        btnAgregar.setTextColor(Color.GRAY)
                        sleep(2000)
                    } finally {
                        btnAgregar.isEnabled = true
                        btnAgregar.setBackgroundResource(R.drawable.btn_style_dk)
                        btnAgregar.setTextColor(Color.BLACK)
                    }
                }
            })*/
            counts[0]++
        }
        if( pss.length < 3 ){
            edTxtPsswrd.error = "!"
            toast.setText("Ingresa una contraseña válida")
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
            toast.show()
            counts[1]++
        }
        if( key != matchKey ){
            edTxtKeyword.error = "Verifica tu palabra clave"
            counts[2]++
        }
        if( counts[0]==0 && counts[1]==0 && counts[2]==0)
            return true
        return false
    }
}