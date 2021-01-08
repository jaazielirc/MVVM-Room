package com.jaax.edsa.controlador

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.jaax.edsa.modelo.DBHelper
import com.jaax.edsa.modelo.Usuario
import com.jaax.edsa.R
import com.jaax.edsa.vista.SupportUser
import java.lang.NullPointerException

class UpdateUsuario: AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var credencial: EditText
    private lateinit var newCredencial: EditText
    private lateinit var confNewCredencial: EditText
    private lateinit var switch: SwitchCompat
    private lateinit var back: ImageView
    private lateinit var btnUpdt: Button
    private lateinit var help: ImageView
    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private lateinit var usuarioActualizado: Usuario

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_usuario)

        supportActionBar!!.title = "EDSA"
        usuario = findViewById(R.id.edituser_username)
        credencial = findViewById(R.id.edituser_credencial)
        newCredencial = findViewById(R.id.edituser_new_credencial)
        confNewCredencial = findViewById(R.id.edituser_confirmar_new_credencial)
        switch = findViewById(R.id.edituser_switch)
        back = findViewById(R.id.edituser_back)
        btnUpdt = findViewById(R.id.edituser_btnActualizar)
        help = findViewById(R.id.edituser_help)
        db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(this.applicationContext, "txt", Toast.LENGTH_LONG)
        initDatos()
    }

    override fun onResume() {
        super.onResume()
        onClickEvents()
    }

    private fun initDatos() {
        val datosUsuario = this.intent.extras
        usuarioActualizado = Usuario(
            datosUsuario?.getString("Login_usrNombre")!!,
            datosUsuario.getString("Login_usrPassword")!!,
            datosUsuario.getString("Login_usrKeyword")!!,
            ArrayList()
        )
        usuario.hint = "Nombre de usuario"
        credencial.hint = "Palabra secreta"
        newCredencial.hint = "Contraseña nueva"
        confNewCredencial.hint = "Confirmar contraseña"
    }

    private fun onClickEvents() {
        opcionesSwitch()
        help.setOnClickListener { SupportUser().show(supportFragmentManager, "helpUser") }
        back.setOnClickListener {
            val intent = Intent(applicationContext, LoginUsuario::class.java)
            intent.putExtra("usrNombre", usuarioActualizado.nombre)
            intent.putExtra("usrPassword", usuarioActualizado.password)
            intent.putExtra("usrKeyword", usuarioActualizado.keyword)
            startActivity(intent)
            this.finish()
        }

        btnUpdt.setOnClickListener {
            val usr = usuario.text.toString()
            val credencial = credencial.text.toString()
            val newCredencial = newCredencial.text.toString()
            val confNewCredencial = confNewCredencial.text.toString()

            if( !switch.isChecked ){ //actualizar contraseña
                usuarioActualizado = Usuario(usr, newCredencial, credencial, ArrayList())

                val acceso = datosValidosPassword(
                    usuarioActualizado.nombre,
                    usuarioActualizado.keyword,
                    usuarioActualizado.password,
                    confNewCredencial
                )

                if( acceso ){
                    val actualizar = actualizarPasswordUsuario(usuarioActualizado)
                    if( actualizar ) {
                        btnUpdt.isEnabled = false
                        btnUpdt.elevation = 5.0F
                        btnUpdt.setBackgroundResource(R.drawable.disable_btn_style)
                        btnUpdt.setTextColor(Color.GRAY)
                        back.visibility = View.GONE
                        toast.setText("Datos actualizados")
                        toast.show()
                        val thread = object : Thread() {
                            override fun run() {
                                try {
                                    sleep(3000)
                                } finally {
                                    val intent = Intent(applicationContext, LoginUsuario::class.java)
                                    intent.putExtra("usrNombre", usuarioActualizado.nombre)
                                    intent.putExtra("usrPassword", usuarioActualizado.password)
                                    intent.putExtra("usrKeyword", usuarioActualizado.keyword)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                        thread.start()
                    } else {
                        toast.setText("Error al actualizar")
                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 300)
                        toast.show()
                    }
                }
            } else {
                usuarioActualizado = Usuario(usr, credencial, newCredencial, ArrayList())

                val acceso = datosValidosKeyword(
                    usuarioActualizado.nombre,
                    usuarioActualizado.password,
                    usuarioActualizado.keyword,
                    confNewCredencial
                )

                if( acceso ){
                    val actualizar = actualizarKeywordUsuario(usuarioActualizado)
                    if( actualizar ) {
                        btnUpdt.isEnabled = false
                        btnUpdt.elevation = 5.0F
                        btnUpdt.setBackgroundResource(R.drawable.disable_btn_style)
                        btnUpdt.setTextColor(Color.GRAY)
                        back.visibility = View.GONE
                        toast.setText("Datos actualizados")
                        toast.show()
                        val thread = object : Thread() {
                            override fun run() {
                                try {
                                    sleep(3000)
                                } finally {
                                    val intent = Intent(applicationContext, LoginUsuario::class.java)
                                    intent.putExtra("usrNombre", usuarioActualizado.nombre)
                                    intent.putExtra("usrPassword", usuarioActualizado.password)
                                    intent.putExtra("usrKeyword", usuarioActualizado.keyword)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                        thread.start()
                    } else {
                        toast.setText("Error al actualizar")
                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 300)
                        toast.show()
                    }
                }
            }
        }
    }

    private fun actualizarPasswordUsuario( user: Usuario ): Boolean {
        val cursor = db.getAllUsuarios()
        var actualizar = false
        try {
            if( cursor.count>0 ){
                while( cursor.moveToNext() ){
                    if( cursor.getString(0)==user.nombre && cursor.getString(2)==user.keyword ){
                        actualizar = db.updtPasswordUsuario(user.nombre, user.password, user.keyword)
                    }
                }
            }
        }catch(ne: NullPointerException){ ne.printStackTrace() }
        return actualizar
    }

    private fun actualizarKeywordUsuario( user: Usuario ): Boolean{
        val cursor = db.getAllUsuarios()
        var actualizar = false
        try {
            if( cursor.count>0 ){
                while( cursor.moveToNext() ){
                    if( cursor.getString(0)==user.nombre && cursor.getString(1)==user.password ){
                        actualizar = db.updtKeywordUsuario(user.nombre, user.password, user.keyword)
                    }
                }
            }
        }catch(ne: NullPointerException){ ne.printStackTrace() }
        return actualizar
    }

    private fun datosValidosPassword(name: String, key: String, newPsswrd: String, confNewPsswrd: String): Boolean{
        val counts = arrayOf(0, 0, 0, 0)
        val regexU = Regex("(?=.*[a-zA-Z])(?=.*\\d)\\S{5,15}" )
        val regexK = Regex("(?=.*[a-zA-Z])(?=.*[,._*%-])\\S{6,15}")
        val regexP = Regex("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[,._*%-])\\S{8,15}")
        toast.setText("Si necesitas ayuda, toca '?' para más información")

        if( name=="" || key=="" || newPsswrd=="" || confNewPsswrd=="" ){
            toast.setText("Uno o más campos están vacíos, si necesitas ayuda toca '?'")
            toast.show()
            return false
        } else {
            if(!name.matches(regexU)){
                counts[0]++
                usuario.error = "!"
                toast.show()
            }
            if(!key.matches(regexK)){
                counts[1]++
                credencial.error = "!"
                toast.show()
            }
            if(!newPsswrd.matches(regexP)){
                counts[2]++
                newCredencial.error = "!"
                toast.show()
            }
            if( confNewPsswrd != newPsswrd ){
                counts[3]++
                confNewCredencial.error = "Las contraseñas no coinciden"
            }
        }
        if( counts[0]==0 && counts[1]==0 && counts[2]==0 && counts[3]==0 )
            return true
        return false
    }

    private fun datosValidosKeyword(name: String, password: String, newKey: String, confNewKey: String): Boolean{
        val counts = arrayOf(0, 0, 0, 0)
        val regexU = Regex("(?=.*[a-zA-Z])(?=.*\\d)\\S{5,15}" )
        val regexP = Regex("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[,._*%-])\\S{8,15}")
        val regexK = Regex("(?=.*[a-zA-Z])(?=.*[,._*%-])\\S{6,15}")
        toast.setText("Si necesitas ayuda, toca '?' para más información")

        if( name=="" || password=="" || newKey=="" || confNewKey=="" ){
            toast.setText("Uno o más campos están vacíos, si necesitas ayuda toca '?'")
            toast.show()
            return false
        } else {
            if(!name.matches(regexU)){
                counts[0]++
                usuario.error = "!"
                toast.show()
            }
            if(!password.matches(regexP)){
                counts[1]++
                credencial.error = "!"
                toast.show()
            }
            if(!newKey.matches(regexK)){
                counts[2]++
                newCredencial.error = "!"
                toast.show()
            }
            if( confNewKey != newKey ){
                counts[3]++
                confNewCredencial.error = "Las palabras no coinciden"
            }
        }
        if( counts[0]==0 && counts[1]==0 && counts[2]==0 && counts[3]==0 )
            return true
        return false
    }

    private fun opcionesSwitch() {

        switch.setOnClickListener {
            if( !switch.isChecked ){
                credencial.setText("")
                newCredencial.setText("")
                confNewCredencial.setText("")
                credencial.hint = "Palabra secreta"
                newCredencial.hint = "Contraseña nueva"
                confNewCredencial.hint = "Confirmar contraseña"
            } else {
                credencial.setText("")
                newCredencial.setText("")
                confNewCredencial.setText("")
                credencial.hint = "Contraseña"
                newCredencial.hint = "Nueva P. secreta"
                confNewCredencial.hint = "Confirmar palabra"
            }
        }
    }

    override fun onBackPressed() {}
}