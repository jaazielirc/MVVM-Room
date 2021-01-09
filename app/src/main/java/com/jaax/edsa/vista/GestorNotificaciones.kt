package com.jaax.edsa.vista

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.widget.RemoteViews
import com.jaax.edsa.R
import com.jaax.edsa.controlador.VerEmails
import java.lang.System.currentTimeMillis

class GestorNotificaciones(private val context: Context, private val usuario: String, private val password: String, private val key: String) {
    companion object {
        private const val CHANNEL = "Emails"
        private const val ID_NOTIFICACION = 0
        private const val REQUEST_CONTENT = 1
        private const val REQUEST_BUBBLE = 2
    }

    private val notificationManager = if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        context.getSystemService(NotificationManager::class.java)
    } else {
        TODO("VERSION.SDK_INT < M")
    }

    init {
        notificarUsuario()
    }

    private fun notificarUsuario() {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            if( notificationManager?.getNotificationChannel(CHANNEL) == null ) {
                val channel = NotificationChannel(
                    CHANNEL,
                    "EDSA",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Necesitas revisar tus datos?"
                }
                notificationManager.createNotificationChannel(channel)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        }
    }

    private fun crearIntent( requestCode: Int ): PendingIntent {
        val intent = Intent(context, VerEmails::class.java)
        intent.putExtra("Login_usrNombre", usuario)
        intent.putExtra("Login_usrPassword", password)
        intent.putExtra("Login_usrKeyword", key)
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun crearNotificacion( mensaje: String, icon: Icon, persona: Person): Notification.Builder {
        return if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                Notification
                    .Builder(context, CHANNEL)
                    .setContentTitle(mensaje)
                    .setSmallIcon(icon)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setStyle(
                        Notification.MessagingStyle(persona)
                            .setGroupConversation(false)
                            .addMessage(mensaje, currentTimeMillis(), persona)
                    )
                    .addPerson(persona)
                    .setShowWhen(true)
                    .setContentIntent(crearIntent(REQUEST_CONTENT))
            } else {
                TODO("VERSION.SDK_INT < P")
            }
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    fun mostrarNotificacion( msj: String ) {
        val icon = if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Icon.createWithResource(context, R.drawable.addmail)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        val persona = crearPersona(icon)
        val notificacion = crearNotificacion(msj, icon, persona)
        val bubbleMetadata = crearBubbleMetadata(icon)
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            notificacion.setBubbleMetadata(bubbleMetadata)
        }
        notificationManager?.notify(ID_NOTIFICACION, notificacion.build())
    }

    private fun crearPersona(icon: Icon): Person {
        return if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            Person.Builder()
                .setName("EDSA")
                .setIcon(icon)
                .setBot(true)
                .setImportant(true)
                .build()
        } else {
            TODO("VERSION.SDK_INT < P")
        }
    }

    private fun crearBubbleMetadata(icon: Icon): Notification.BubbleMetadata {
        return if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                Notification.BubbleMetadata.Builder(
                    crearIntent(REQUEST_BUBBLE), icon)
                    .setDesiredHeight(200)
                    .setAutoExpandBubble(false)
                    .setSuppressNotification(true)
                    .build()
            } else {
                TODO("VERSION.SDK_INT < R")
            }
        } else {
            TODO("VERSION.SDK_INT < Q")
        }
    }
}