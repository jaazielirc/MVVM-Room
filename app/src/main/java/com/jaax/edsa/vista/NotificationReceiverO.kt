package com.jaax.edsa.vista

import com.jaax.edsa.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jaax.edsa.modelo.Cuenta
import com.jaax.edsa.modelo.DBHelper
import com.jaax.edsa.modelo.Email
import com.jaax.edsa.modelo.Usuario

class NotificationReceiverO: BroadcastReceiver() {
    companion object {
        private const val KEY_NOTIFICATION_ID = "key_notification_id"
        private const val KEY_MESSAGE_ID = "key_message_id"
    }

    //como vamos a utilizar la misma notificación y la vamos a actualizar, le enviamos a sí misma los datos recogidos con los 'KEY' correctos
    fun getDataIntent(context: Context, notificationId: Int, messageId: Int): Intent {
        val intent = Intent(context, NotificationReceiverO::class.java)
        intent.action = GestorNotificaciones.REPLY_ACTION
        intent.putExtra(KEY_NOTIFICATION_ID, notificationId)
        intent.putExtra(KEY_MESSAGE_ID, messageId)

        return intent
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if( intent?.action.equals(GestorNotificaciones.REPLY_ACTION) ){
            var message = GestorNotificaciones(context!!).getReplyMessage(intent).toString()
            val messageId = intent?.getIntExtra(KEY_MESSAGE_ID, 0)
            message = message.replace(" ", "") //quitamos espacios en blanco

            val respuesta = getQueryResultRemoteInput(context, message)

            //aquí podemos poner lo que recibimos
            updateNotification(context, messageId!!, respuesta)

            //crea otra notificacion con las respuestas encontradas
            GestorNotificaciones(context).mostrarNotificacion()
            GestorNotificaciones(context).createNotificacionChannel()
        }
    }

    //no usaremos otro ID pues no estamos enviando anda
    private fun updateNotification(context: Context, notificationId: Int, respuestas: Array<String>) {
        val notificationManager = NotificationManagerCompat.from(context)
        val builder = NotificationCompat
            .Builder(context, GestorNotificaciones.CHANNEL)
            .setSmallIcon(R.drawable.addmail)
            .setContentTitle(respuestas[0])
            .setContentText("Contraseña: ${respuestas[1]}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, builder.build())
    }

    private fun getQueryResultRemoteInput(context: Context, queryToSearch: String): Array<String> {
        val db = DBHelper(context, DBHelper.nombreDB, null, DBHelper.version)
        val nombreUsuario = getIdUsuario(context)

        //es necesario verificar si se trata de una busqueda de email o simplemente una cuenta asignada a un email
        if( !queryToSearch.contains("~") ){
            val cursor = db.getEmailsById(nombreUsuario!!)
            val email = Email("?", "?", "?", ArrayList())
            try {
                if( cursor.count>0 ){
                    while( cursor.moveToNext() ){
                        email.ID = cursor.getString(0)
                        email.nombre = cursor.getString(1)
                        email.passwrd = cursor.getString(2)
                    }
                }
            } catch(sql: SQLiteException) {
                Toast.makeText(context, "No se encontró ese email", Toast.LENGTH_SHORT).show()
            }
            return arrayOf(email.nombre, email.passwrd)
        } else {
            val separarQuery = queryToSearch.split("~")
            val emailQuery = separarQuery[0]
            val cuentaQuery = separarQuery[1]
            val cursor = db.getDatosCuentaById(emailQuery, cuentaQuery)
            val cuenta = Cuenta(emailQuery, "?", "?", cuentaQuery)

            try {
                if( cursor.count>0 ){
                    while( cursor.moveToNext() ){
                        cuenta.usuario = cursor.getString(1)
                        cuenta.passwrd = cursor.getString(2)
                        cuenta.tipo = cursor.getString(3)
                    }
                }
            } catch(sql: SQLiteException){
                Toast.makeText(context, "No se encontró esa cuenta", Toast.LENGTH_SHORT).show()
            }
            return arrayOf(cuenta.usuario, cuenta.passwrd)
        }
    }

    private fun getIdUsuario(context: Context): String? {
        val db = DBHelper(context, DBHelper.nombreDB, null, DBHelper.version)
        val cursorUsuario = db.getAllUsuarios()
        var usuario = Usuario("?", "?", "?", ArrayList())
        if( cursorUsuario.count>0 ){
            while(cursorUsuario.moveToNext()){
                usuario = Usuario(cursorUsuario.getString(0), cursorUsuario.getString(1), cursorUsuario.getString(2), ArrayList())
            }
            return usuario.nombre
        }
        return null
    }
}