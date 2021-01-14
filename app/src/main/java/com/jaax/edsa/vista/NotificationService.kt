package com.jaax.edsa.vista

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast

class NotificationService: Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(applicationContext, "Buscador EDSA ejecutándose en segundo plano", Toast.LENGTH_SHORT).show()
        GestorNotificaciones(this).mostrarNotificacion()
        GestorNotificaciones(this).createNotificacionChannel()
    }
}