package com.jaax.edsa.controlador

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.jaax.edsa.modelo.DBHelper
import com.jaax.edsa.modelo.Usuario
import com.jaax.edsa.R
import com.jaax.edsa.vista.SupportUser
import java.lang.NullPointerException

class RegistrarUsuario: AppCompatActivity() {
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
        setContentView(R.layout.reg_usuario)

        supportActionBar!!.title = "Registrar usuario"
        user = findViewById(R.id.usr_reg_username)
        psswrd = findViewById(R.id.usr_reg_password)
        keyword = findViewById(R.id.usr_reg_keyword)
        btnReg = findViewById(R.id.usr_reg_btnRegistrar)
        help = findViewById(R.id.usr_reg_help)
        db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(this.applicationContext, "txt", Toast.LENGTH_LONG)

        user.setText("jaax1")
        psswrd.setText("j44x.EDSA")
        keyword.setText("deve.loper")
    }

    override fun onResume() {
        super.onResume()
        help.setOnClickListener { SupportUser().show(supportFragmentManager, "helpUser") }

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
                    btnReg.setBackgroundResource(R.drawable.disable_btn_style)
                    btnReg.setTextColor(Color.GRAY)
                    toast.setText("Registro exitoso\nRedirigiendo al inicio...")
                    toast.show()
                    val thread = object : Thread() {
                        override fun run() {
                            try {
                                sleep(3000)
                            } finally {
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                intent.putExtra("usuarioActual", usuario.nombre)
                                startActivity(intent)
                                finish()
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

    private fun registrarUsuario( user: Usuario ): Boolean{
        val cursor = db.getAllUsuarios() //por si ya existe ese usuario
        val listaUsrs = arrayListOf<String>()
        var i = 0
        var insertar = false
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        try {
            if( cursor.count>0 ){
                while( cursor.moveToNext() ){
                    listaUsrs.add(cursor.getString(0)) //llenar con usuarios si ya existen
                    if( listaUsrs.get(i).equals(user.nombre) ){
                        i = 0
                        break
                    }
                    i++
                }
            } else insertar = db.insertarUsuario(user.nombre, user.password, user.keyword) //primer cuenta agregada
            if( i != 0 ) insertar = db.insertarUsuario(user.nombre, user.password, user.keyword)

        } catch (ne: NullPointerException){}
        return insertar
    }

    private fun datosValidos(name: String, pss: String, key: String): Boolean{
        val counts = arrayOf(0, 0, 0)
        val regexU = Regex("(?=.*[a-zA-Z])(?=.*\\d)\\S{5,15}" )
        val regexP = Regex("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[,._*%-])\\S{8,15}")
        val regexK = Regex("(?=.*[a-zA-Z])(?=.*[,._*%-])\\S{6,15}")
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this.applicationContext, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}