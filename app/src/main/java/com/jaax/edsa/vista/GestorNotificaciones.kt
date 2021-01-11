package com.jaax.edsa.vista

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.jaax.edsa.R
import com.jaax.edsa.modelo.DBHelper
import com.jaax.edsa.modelo.Usuario

class GestorNotificaciones(private val context: Context) {
    //variables estaticas publicas y privadas
    companion object {
        private const val KEY_TEXT_REPLY = "key_text_reply"

        //CANAL DE COMUNICACIONES PARA LAS NOTIFICACIONES
        //SOLO DISPONIBLE EN ANDROID 8 O MAYOR
        const val CHANNEL = "findEmails"

        //parametros de la notificacion
        const val NOTIFICATION_ID = 1000
        const val MESSAGE_ID = 0
        const val REPLY_ACTION = "replyGestorNotificaciones"
    }

    // SE CREA EL Pending Intent PARA CUANDO SE INGRESE TEXTO A ENVIAR
    private fun createIntentToNotification(): PendingIntent {
        val intent: Intent?

        // ANDROID >= 8 SE REQUIERE EL BroadcastReceiver
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent = NotificationReceiverO().getDataIntent(context, NOTIFICATION_ID, MESSAGE_ID)
            PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            // ANDROID < 8 UTILIZARÁ UNA NOTIFICACIÓN PERSONALIZADA
            intent = NotificationReplyN.getReplyMessageIntent(context, NOTIFICATION_ID, MESSAGE_ID)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    fun mostrarNotificacion() {
        // se crea un RemoteInput para ingresar texto en la notificación
        val remoteInput = RemoteInput
            .Builder(KEY_TEXT_REPLY)
            .setLabel("Introduce un email válido")
            .build()

        //crear el intento pendiente del texto a ingresar
        val pendingIntent = createIntentToNotification()

        // creamos el botón de acción para buscar
        val action = NotificationCompat.Action
            .Builder(R.drawable.baseline_search_black_18dp, "Ingresa tu email", pendingIntent)
            .addRemoteInput(remoteInput)
            .setAllowGeneratedReplies(true)
            .build()

        // creamos la notificación
        val builder = NotificationCompat
            .Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_baseline_search_24)
            .setContentTitle("Buscar email...")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .addAction(action)

        //se crea el objeto manager de notificacion y se manda la notificacion
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun createNotificacionChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val nombre = "EDSA ejecutándose"
            val descripcion = "DescriptionChannel"
            val notificationChannel = NotificationChannel(
                CHANNEL,
                nombre,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = descripcion

            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    // obtiene el mensaje recibido tras ingresar texto
    fun getReplyMessage(intent: Intent?): CharSequence? {
        val bundle = RemoteInput.getResultsFromIntent(intent)
        if( bundle != null ){
            return bundle.getCharSequence(KEY_TEXT_REPLY)
        }
        return null
    }
}