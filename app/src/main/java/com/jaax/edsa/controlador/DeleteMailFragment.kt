package com.jaax.edsa.controlador

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.jaax.edsa.modelo.DBHelper
import com.jaax.edsa.modelo.Email
import com.jaax.edsa.R
import java.lang.ClassCastException

class DeleteMailFragment( private val ID: String, private val nombre: String ): DialogFragment(){
    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private lateinit var email: Email
    private lateinit var callBack: OnCallbackReceivedDel

    @SuppressLint("ShowToast")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        db = DBHelper(activity!!.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(activity!!.applicationContext, "txt", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        email = Email(ID, nombre, "", ArrayList())
        val msj = "También se eliminarán las cuentas asociadas"
        val builder = AlertDialog.Builder(activity)
        builder
            .setTitle("¿Eliminar ${email.nombre}?")
            .setMessage(msj)
            .setIcon(R.drawable.baseline_delete_black_18dp)
            .setPositiveButton("Eliminar") { _, _ ->
                val delete = eliminarEmail(email)
                if( delete ){
                    toast.setText("Email eliminado")
                    toast.show()
                } else {
                    toast.setText("Error al eliminar\nIntenta nuevamente")
                    toast.show()
                }
            }
            .setNegativeButton("Cancelar") {_, _ -> dismiss() }

        return builder.create()
    }

    interface OnCallbackReceivedDel {
        fun refreshByDeleting()
    }

    override fun onDestroy() {
        super.onDestroy()
        try{
            callBack = context as OnCallbackReceivedDel
        }catch (cce: ClassCastException){cce.printStackTrace()}
        callBack.refreshByDeleting()
    }

    private fun eliminarEmail(delEmail: Email): Boolean {
        val cursor = db.getDatosEmailById(delEmail.ID, delEmail.nombre) //debe haber sólo 1 email si existe
        val cursor2 = db.getCuentasById(delEmail.nombre)
        var eliminar = false
        var eliminar2 = false
        try {
            if(cursor.count>0){ eliminar = db.delEmail(delEmail.ID, delEmail.nombre) }

            eliminar2 = if(cursor2.count>0){
                db.truncateTablePerParentDeleted(delEmail.nombre, 0)
            } else {
                true //el email no tiene cuentas agregadas
            }
        }catch(sql: SQLiteException){sql.printStackTrace()}

        return (eliminar && eliminar2)
    }
}