package com.jaax.edsa.controlador

import android.app.Application
import com.jaax.edsa.modelo.DBHComponent
import com.jaax.edsa.modelo.DBHModulo
import com.jaax.edsa.modelo.DaggerDBHComponent

class App: Application() {
    lateinit var dbhComponent: DBHComponent

    override fun onCreate() {
        super.onCreate()
        dbhComponent = DaggerDBHComponent
            .builder()
            .dBHModulo( DBHModulo(applicationContext) )
            .build()
    }

    fun getDBHelper() = dbhComponent
}