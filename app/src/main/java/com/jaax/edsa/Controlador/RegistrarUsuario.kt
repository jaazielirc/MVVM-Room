package com.jaax.edsa.Controlador

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.jaax.edsa.Modelo.DBHelper
import com.jaax.edsa.Modelo.Usuario
import com.jaax.edsa.R
import com.jaax.edsa.Vista.SupportUser
import java.lang.NullPointerException

class RegistrarUsuario: AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var psswrd: EditText
    private lateinit var keyword: EditText
    private lateinit var btnReg: Button
    private lateinit var help: ImageView
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reg_usuario)

        supportActionBar!!.setTitle("Usuario nuevo")
        supportActionBar!!.setLogo(R.mipmap.ic_launcher_round)
        usuario = findViewById(R.id.usr_reg_username)
        psswrd = findViewById(R.id.usr_reg_password)
        keyword = findViewById(R.id.usr_reg_keyword)
        btnReg = findViewById(R.id.usr_reg_btnRegistrar)
        help = findViewById(R.id.usr_reg_help)
        db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)

        usuario.setText("jaax1")
        psswrd.setText("j44x.EDSA")
        keyword.setText("deve.loper")
    }

    override fun onResume() {
        super.onResume()
        help.setOnClickListener { SupportUser().show(supportFragmentManager, "helpUser") }

        btnReg.setOnClickListener {
            val usr = usuario.text.toString()
            val pss = psswrd.text.toString()
            val key = keyword.text.toString()
            val usuario = Usuario(usr, pss, key, ArrayList())
            val acceso = datosValidos(usuario.nombre, usuario.password, usuario.keyword)
            if( acceso ){
                val registro = registrarUsuario(usuario)
                if( registro ) {
                    val thread = object : Thread() {
                        override fun run() {
                            try {
                                sleep(2500)
                            } finally {
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                intent.putExtra("usuarioActual", usuario.nombre)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    thread.start()
                }
            }
        }
    }

    private fun registrarUsuario( user: Usuario ): Boolean{
        val cursor = db.getAllUsuarios() //por si ya existe ese usuario
        val listaUsrs = arrayListOf<String>()
        var i = 0
        var insertar = false
        try {
            if( cursor.count>0 ){
                while( cursor.moveToNext() ){
                    listaUsrs.add(cursor.getString(0)) //llenar con usuarios si ya existen
                    if( listaUsrs.get(i).equals(user.nombre) ){
                        Toast.makeText(this.applicationContext, "Ese usuario no está disponible", Toast.LENGTH_SHORT).show()
                        i = 0
                        break
                    }
                    i++
                }
            } else insertar = db.insertarUsuario(user.nombre, user.password, user.keyword) //primer cuenta agregada
            if( i != 0 ) insertar = db.insertarUsuario(user.nombre, user.password, user.keyword)

            if(insertar)
                Toast.makeText(this.applicationContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this.applicationContext, "Error al registrar", Toast.LENGTH_SHORT).show()
        } catch (ne: NullPointerException){}

        return insertar
    }

    private fun datosValidos(name: String, pss: String, key: String): Boolean{
        var n = 0
        var p = 0
        var k = 0
        val toast = Toast.makeText(this.applicationContext, "Si estás en problemas, toca '?' para más información", Toast.LENGTH_LONG)
        val regexU = Regex("(?=.*[a-zA-Z])(?=.*\\d)\\S{5,15}" )
        val regexP = Regex("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[,._*%-])\\S{8,15}")
        val regexK = Regex("(?=.*[a-zA-Z])(?=.*[,._*%-])\\S{6,15}")
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)

        if( name=="" || pss=="" || key=="" ){
            toast.setText("Uno o más campos están vacíos, si necesitas ayuda toca '?'")
            toast.show()
            return false
        } else {
            if(!name.matches(regexU)){
                n++
                usuario.error = "Ups"
                toast.show()
            }
            if(!pss.matches(regexP)){
                p++
                psswrd.error = "Ups"
                toast.show()
            }
            if(!key.matches(regexK)){
                k++
                keyword.error = "Ups"
                toast.show()
            }
        }
        if( n==0 && p==0 && k==0 )
            return true
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this.applicationContext, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}