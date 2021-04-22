package com.jaax.edsa.controlador

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
import com.jaax.edsa.R
import com.jaax.edsa.modelo.Cuenta
import com.jaax.edsa.modelo.DBHelper
import java.sql.SQLException

class UpdateCuentaFragment(cuentaForUpdate: Cuenta): DialogFragment() {

    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private lateinit var edTxtUsuario: EditText
    private lateinit var edTxtPsswrd: EditText
    private lateinit var edTxtTipo: EditText
    private lateinit var btnModificar: Button
    private lateinit var toggleUser: ToggleButton
    private lateinit var togglePsswrd: ToggleButton
    private lateinit var toggleTipo: ToggleButton
    private var cuenta: Cuenta = cuentaForUpdate

    @SuppressLint("ShowToast")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.edit_account, null)
        val builder = AlertDialog.Builder(activity)

        edTxtUsuario = view.findViewById(R.id.edacc_usuario)
        edTxtPsswrd = view.findViewById(R.id.edacc_psswrd)
        edTxtTipo = view.findViewById(R.id.edacc_tipo)
        btnModificar = view.findViewById(R.id.edacc_btn)
        toggleUser = view.findViewById(R.id.edacc_toggle_usuario)
        togglePsswrd = view.findViewById(R.id.edacc_toggle_psswrd)
        toggleTipo = view.findViewById(R.id.edacc_toggle_tipo)
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
        edTxtUsuario.setText( cuenta.usuario )
        edTxtPsswrd.setText( cuenta.passwrd )
        edTxtTipo.setText( cuenta.tipo )
    }

    private fun onClick() {
        btnModificar.setOnClickListener {
            val edTextUser = edTxtUsuario.text.toString()
            val edTextPass = edTxtPsswrd.text.toString()
            val edTextType = edTxtTipo.text.toString()
            val newCuenta = Cuenta( cuenta.ID, edTextUser, edTextPass, edTextType )

            val acceso = datosValidos(newCuenta.usuario, newCuenta.passwrd, cuenta.tipo)

            if(acceso){
                val edicion = modificarCuenta(cuenta, newCuenta)
                if(edicion){
                    toast.setText("Cuenta actualizada")
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0 ,0)
                    toast.show()
                    dismiss()
                } else {
                    if( (cuenta.usuario==newCuenta.usuario) && (cuenta.passwrd==newCuenta.passwrd) && (cuenta.tipo==newCuenta.tipo) ){
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
        togglePsswrd.setOnCheckedChangeListener { _, isChecked ->
            if( isChecked  ){
                edTxtPsswrd.isEnabled = true
                edTxtPsswrd.requestFocus()
            } else {
                edTxtPsswrd.isEnabled = false
            }
        }
        toggleTipo.setOnCheckedChangeListener { _, isChecked ->
            if( isChecked  ){
                edTxtTipo.isEnabled = true
                edTxtTipo.requestFocus()
            } else {
                edTxtTipo.isEnabled = false
            }
        }
    }

    private fun modificarCuenta(accountAddress: Cuenta, newAccountAddress: Cuenta): Boolean{
        val cursor = db.getDatosCuentaById(accountAddress.ID, accountAddress.tipo) //debe haber sólo 1 email si existe
        var actualizar = false
        try {
            if( cursor.count>0 ){
                if( (accountAddress.usuario==newAccountAddress.usuario) &&
                    (accountAddress.passwrd==newAccountAddress.passwrd) &&
                    (accountAddress.tipo==newAccountAddress.tipo)){
                    return actualizar
                } else {
                    actualizar = db.updtDatosCuenta(
                        accountAddress.ID,
                        accountAddress.usuario,
                        newAccountAddress.usuario,
                        newAccountAddress.passwrd,
                        accountAddress.tipo,
                        newAccountAddress.tipo
                    )
                }
            } else {
                toast.setText("No existe esa cuenta (?")
                toast.show()
            }
        } catch (ne: SQLException){ne.printStackTrace()}
        return actualizar
    }

    private fun datosValidos(usr: String, pss: String, tipo: String): Boolean{

        val counts = arrayOf(0, 0, 0)
        if(usr.isEmpty()){
            edTxtUsuario.error = "!"
            toast.setText("Ingresa un usuario ó email válido")
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
        if(tipo.isEmpty()){
            edTxtTipo.error = "!"
            counts[2]++
        }
        if(counts[0]==0 && counts[1]==0 && counts[2]==0)
            return true
        return false
    }
}