package com.jaax.edsa

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.lang.NullPointerException

class AddUsuario: AppCompatActivity() {
    private lateinit var user: EditText
    private lateinit var psswrd: EditText
    private lateinit var keyword: EditText
    private lateinit var btnReg: Button
    private lateinit var help: ImageView
    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private lateinit var usuario: Usuario

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_usuario)

        supportActionBar!!.title = "Registrar usuario"
        user = findViewById(R.id.usr_reg_username)
        psswrd = findViewById(R.id.usr_reg_password)
        keyword = findViewById(R.id.usr_reg_keyword)
        btnReg = findViewById(R.id.usr_reg_btnRegistrar)
        help = findViewById(R.id.usr_reg_help)
        toast = Toast.makeText(this.applicationContext, "txt", Toast.LENGTH_LONG)
        db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
    }

    override fun onResume() {
        super.onResume()

        btnReg.setOnClickListener {
            val usr = user.text.toString()
            val pss = psswrd.text.toString()
            val key = keyword.text.toString()
            usuario = Usuario(usr, pss, key, ArrayList())
            val acceso = datosValidos(usuario.nombre, usuario.password, usuario.keyword)
            if( acceso ){
                val registro = registrarUsuario(usuario)
                if( registro ) {
                    btnReg.isEnabled = false
                    btnReg.elevation = 5F
                    btnReg.setBackgroundResource(R.drawable.btn_disable_style)
                    btnReg.setTextColor(Color.GRAY)
                    toast.setText("Registro exitoso\nRedirigiendo al inicio...")
                    toast.show()
                    val thread = object : Thread() {
                        override fun run() {
                            try {
                                sleep(3000)
                            } finally {
                                val intent = Intent(applicationContext, LoginUsuario::class.java)
                                intent.putExtra("usrNombre", usuario.nombre)
                                intent.putExtra("usrPassword", usuario.password)
                                intent.putExtra("usrKeyword", usuario.keyword)
                                startActivity(intent)
                                this@AddUsuario.finish()
                            }
                        }
                    }
                    thread.start()
                } else {
                    toast.setText("Elige otro nombre de usuario")
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 230)
                    toast.show()
                }
            }
        }
    }

    private fun registrarUsuario( user: Usuario): Boolean{
        var insertar = false
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        try {
            insertar = db.insertarUsuario(user.nombre, user.password, user.keyword)
        } catch (ne: NullPointerException){}
        return insertar
    }

    private fun datosValidos(name: String, pss: String, key: String): Boolean{
        val counts = arrayOf(0, 0, 0)
        val regexU = Regex("(?=.*[a-zA-Z])\\S{4,15}" ) //puede aceptar letras
        val regexP = Regex("(?=.*[a-zA-Z])\\S{6,100}")
        val regexK = Regex("(?=.*[a-zA-Z])\\S{4,15}")

        //val regexP = Regex("(?=.*[a-zA-Z])(?=.*\\d)(?=.*[,._*%-])\\S{8,100}")
        //val regexK = Regex("(?=.*[a-zA-Z])(?=.*[,._*%-])\\S{6,15}")
        toast.setText("Si necesitas ayuda, toca '?' para más información")

        if( name=="" || pss=="" || key=="" ){
            toast.setText("Uno o más campos están vacíos, si necesitas ayuda toca '?'")
            toast.show()
            return false
        } else {
            if(!name.matches(regexU)){
                counts[0]++
                user.error = "!"
                toast.show()
            }
            if(!pss.matches(regexP)){
                counts[1]++
                psswrd.error = "!"
                toast.show()
            }
            if(!key.matches(regexK)){
                counts[2]++
                keyword.error = "!"
                toast.show()
            }
        }
        if( counts[0]==0 && counts[1]==0 && counts[2]==0 )
            return true
        return false
    }
}