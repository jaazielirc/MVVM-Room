package com.jaax.edsa

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar
import com.jaax.edsa.GestorNotificaciones.Companion.REPLY_ACTION

class NotificationReplyN: AppCompatActivity() {
    private var messageId = 0
    private var notifyId = 0
    private lateinit var mSendButton: ImageButton
    private lateinit var mEditReply: EditText
    private lateinit var relativeLayout: RelativeLayout

    companion object {
        private const val KEY_MESSAGE_ID = "key_message_id"
        private const val KEY_NOTIFY_ID = "key_notify_id"

        fun getReplyMessageIntent(context: Context?, notifyId: Int, messageId: Int): Intent {
            val intent = Intent(context, NotificationReplyN::class.java)
            intent.action = REPLY_ACTION
            intent.putExtra(KEY_MESSAGE_ID, messageId)
            intent.putExtra(KEY_NOTIFY_ID, notifyId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        if(REPLY_ACTION == intent.action) {
            messageId = intent.getIntExtra(KEY_MESSAGE_ID, 0)
            notifyId = intent.getIntExtra(KEY_NOTIFY_ID, 0)
        }
        mEditReply = findViewById<View>(R.id.edit_reply) as EditText
        mSendButton = findViewById<View>(R.id.button_send) as ImageButton
        mSendButton.setOnClickListener {
            Toast.makeText(this, "SENT", Toast.LENGTH_SHORT).show()
            sendMessage(notifyId)
        }
        relativeLayout = findViewById(R.id.activity_reply)
    }

    private fun sendMessage(messageId: Int) {
        var message = mEditReply.text.toString()
        message = message.replace(" ", "")
        val arrayRespuestas = getQueryResultRemoteInput(this, message)


        updateNotification(this, notifyId, arrayRespuestas)

        Snackbar
            .make(relativeLayout,"SNACK: $messageId -> $message", Snackbar.LENGTH_SHORT )
            .setAction("OK") { finish() }
            .show()
    }

    private fun updateNotification(context: Context, notifyId: Int, respuestas: Array<String>) {
        val notificationManager = NotificationManagerCompat.from(context)
        val builder = NotificationCompat
            .Builder(context, GestorNotificaciones.CHANNEL)
            .setSmallIcon(R.drawable.addmail)
            .setContentTitle(respuestas[0])
            .setContentText("Contraseña: ${respuestas[1]}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notifyId, builder.build())
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