package com.jaax.edsa.vista

import com.jaax.edsa.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

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
            val message = GestorNotificaciones(context!!).getReplyMessage(intent)
            val messageId = intent?.getIntExtra(KEY_MESSAGE_ID, 0)

            //aquí podemos poner lo que recibimos
            updateNotification(context, messageId!!, message.toString())

            //crea otra notificacion con las respuestas encontradas
            GestorNotificaciones(context).mostrarNotificacion()
            GestorNotificaciones(context).createNotificacionChannel()
        }
    }

    //no usaremos otro ID pues no estamos enviando anda
    private fun updateNotification(context: Context, notificationId: Int, message: String) {
        val notificationManager = NotificationManagerCompat.from(context)
        val builder = NotificationCompat
            .Builder(context, GestorNotificaciones.CHANNEL)
            .setSmallIcon(R.drawable.addmail)
            .setContentTitle(message)
            .setContentText("AQUI VA TU CONTRASEÑA")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, builder.build())
    }
}