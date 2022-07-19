package com.jaax.edsa

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.DialogFragment
import com.jaax.edsa.data.model.Email
import com.jaax.edsa.data.model.User
import java.lang.ClassCastException
import java.sql.SQLException

class UpdateMailFragment(emailForUpdate: Email, usrForKeyword: User): DialogFragment(){
    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private lateinit var edTxtEmail: EditText
    private lateinit var edTxtPsswrd: EditText
    private lateinit var edTxtKeyword: EditText
    private lateinit var btnModificar: Button
    private lateinit var toggleNombre: ToggleButton
    private lateinit var togglePsswrd: ToggleButton
    private lateinit var bundleN: String
    private lateinit var bundleP: String
    private lateinit var callBack: OnCallbackReceivedEdit
    private var email: Email
    private var user: User

    init {
        this.email = emailForUpdate
        this.user = usrForKeyword
    }

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

    interface OnCallbackReceivedEdit {
        fun refreshByEditing()
    }
    override fun onDestroy() {
        super.onDestroy()
        try{
            callBack = context as OnCallbackReceivedEdit
        }catch (cce: ClassCastException){cce.printStackTrace()}
        callBack.refreshByEditing()
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
        }
    }

    private fun onClick() {
        btnModificar.setOnClickListener {
            val edTextUser = edTxtEmail.text.toString()
            val edTextPass = edTxtPsswrd.text.toString()
            val edTextKey = edTxtKeyword.text.toString()
            val newEmail = Email( user.nombre, edTextUser, edTextPass, ArrayList() )

            val acceso = datosValidos(newEmail.nombre, newEmail.passwrd, user.keyword, edTextKey)

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

    private fun modificarEmail(emailAddress: Email, newEmailAddrss: Email): Boolean{
        val cursor = db.getDatosEmailById(emailAddress.ID, emailAddress.nombre) //debe haber sólo 1 email si existe
        val cursor2 = db.getCuentasById(emailAddress.nombre) // nombreEmail == idCuenta
        var actualizar = false
        var actualizar2 = false

        try {
            if( cursor.count>0 ){
                if( (emailAddress.nombre==newEmailAddrss.nombre) && (emailAddress.passwrd==newEmailAddrss.passwrd) ){
                    return false
                } else {
                    actualizar = db.updtDatosEmail( emailAddress.ID, emailAddress.nombre, newEmailAddrss.nombre, newEmailAddrss.passwrd )
                }
            }
            if( cursor2.count>0 ) { actualizar2 = db.updateChildIdPerParentId(emailAddress.nombre, newEmailAddrss.nombre, 0) }
        } catch (ne: SQLException){ne.printStackTrace()}

        return (actualizar && actualizar2)
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