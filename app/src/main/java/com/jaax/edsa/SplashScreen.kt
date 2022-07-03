package com.jaax.edsa

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashScreen: AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var usuario: Usuario
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        db = DBHelper( this, DBHelper.nombreDB, null, DBHelper.version )

        val thread = object : Thread(){
            override fun run() {
                try {
                    sleep(3000)
                } finally {
                    val intent = Intent(applicationContext, LoginUsuario::class.java)
                    usuario = usuarioYaRegistrado()
                    if( usuario.nombre != "R" ){ //significa que si existe un usuario
                        intent.putExtra("usrNombre", usuario.nombre)
                        intent.putExtra("usrPassword", usuario.password)
                        intent.putExtra("usrKeyword", usuario.keyword)
                    }
                    startActivity(intent)
                    this@SplashScreen.finish()
                }
            }
        }
        thread.start()
    }

    private fun usuarioYaRegistrado(): Usuario {
        try {
            db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        } catch(sqle: SQLiteException){
            sqle.printStackTrace()
        }
        val cursor = db.getAllUsuarios()
        var usr = Usuario("?", "?", "?", ArrayList())

        if(cursor.count>0) {
            while( cursor.moveToNext() ){
                usr = Usuario(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    ArrayList()
                )
            }
        } else {
            usr.nombre = "R"
        }
        return usr
    }
}