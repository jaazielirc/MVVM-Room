package com.jaax.edsa

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class DeleteMailFragment(emailForDelete: Email, usrForKeyword: Usuario): DialogFragment(){
    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private lateinit var txtEmail: TextView
    private lateinit var keyword: EditText
    private lateinit var btnDelete: Button
    private lateinit var callBack: OnCallbackReceivedDel
    private var email: Email
    private var usuario: Usuario

    init {
        this.email = emailForDelete
        this.usuario = usrForKeyword
    }

    interface OnCallbackReceivedDel {
        fun refreshByDeleting()
    }

    @SuppressLint("ShowToast")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.del_email, null)
        val builder = AlertDialog.Builder(activity)

        txtEmail = view.findViewById(R.id.deletequestion)
        keyword = view.findViewById(R.id.email_del_keyword)
        btnDelete = view.findViewById(R.id.email_del_btnEliminar)
        db = DBHelper(activity!!.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(activity!!.applicationContext, "txt", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        val del = "Eliminar ${email.nombre} ?"
        txtEmail.text = del
        builder.setView(view)
        return builder.create()
    }

    override fun onResume() {
        super.onResume()
        btnDelete.setOnClickListener {
            val keyTyped = keyword.text.toString()
            if( usuario.keyword==keyTyped ){
               val deleteOk = eliminarEmail(email)
               if( deleteOk ){
                   toast.setText("Email eliminado")
                   toast.show()
                   this.dismiss()
               } else {
                   toast.setText("Error al eliminar")
                   toast.show()
                   this.dismiss()
               }
            } else {
                keyword.error = "!"
                toast.setText("Verifica tus datos")
                toast.show()
            }
        }
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
                db.truncateTablePerParentDeleted(delEmail.nombre, 0) //elimina las cuentas por ID de Email
            } else {
                true //el email no tiene cuentas agregadas
            }
        }catch(sql: SQLiteException){sql.printStackTrace()}

        return (eliminar && eliminar2)
    }
}