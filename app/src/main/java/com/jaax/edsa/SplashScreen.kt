package com.jaax.edsa

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaax.edsa.data.model.User

class SplashScreen: AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var user: User
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
                    user = usuarioYaRegistrado()
                    if( user.nombre != "R" ){ //significa que si existe un usuario
                        intent.putExtra("usrNombre", user.nombre)
                        intent.putExtra("usrPassword", user.password)
                        intent.putExtra("usrKeyword", user.keyword)
                    }
                    startActivity(intent)
                    this@SplashScreen.finish()
                }
            }
        }
        thread.start()
    }

    private fun usuarioYaRegistrado(): User {
        try {
            db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        } catch(sqle: SQLiteException){
            sqle.printStackTrace()
        }
        val cursor = db.getAllUsuarios()
        var usr = User("?", "?", "?", ArrayList())

        if(cursor.count>0) {
            while( cursor.moveToNext() ){
                usr = User(
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