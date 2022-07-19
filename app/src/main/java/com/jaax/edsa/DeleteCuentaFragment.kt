package com.jaax.edsa

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.jaax.edsa.data.model.Account

class DeleteCuentaFragment(accountForDelete: Account): DialogFragment() {

    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private var account: Account = accountForDelete

    @SuppressLint("ShowToast")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        db = DBHelper(activity!!.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(activity!!.applicationContext, "txt", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)

        val builder = AlertDialog.Builder(activity)
        builder
            .setMessage("¿Eliminar\n${account.usuario} : ${account.tipo}?")
            .setIcon(R.drawable.baseline_delete_black_18dp)
            .setPositiveButton("Eliminar") { _, _ ->
                val delete = eliminarCuenta(account)
                if( delete ){
                    toast.setText("Cuenta eliminada")
                    toast.show()
                } else {
                    toast.setText("Error al eliminar\nIntenta nuevamente")
                    toast.show()
                }
            }
            .setNegativeButton("Cancelar") {_, _ -> dismiss() }

        return builder.create()
    }

    private fun eliminarCuenta(delAccount: Account): Boolean {
        val cursor = db.getDatosCuentaById(delAccount.ID, delAccount.tipo) //debe haber sólo 1 email si existe
        var eliminar = false
        try {
            if(cursor.count>0){
                if( cursor.count>0 ){
                    eliminar = db.delCuenta(delAccount.ID, delAccount.tipo)
                    return eliminar
                } else {
                    toast.setText("Esa cuenta no existe (?")
                    toast.show()
                }
            }
        }catch(sql: SQLiteException){sql.printStackTrace()}
        return eliminar
    }
}