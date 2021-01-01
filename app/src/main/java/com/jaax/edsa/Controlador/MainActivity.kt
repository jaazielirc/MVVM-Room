package com.jaax.edsa.Controlador

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.jaax.edsa.Modelo.DBHelper
import com.jaax.edsa.Modelo.Usuario
import com.jaax.edsa.R
import com.jaax.edsa.Vista.VerEmails
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {
    private lateinit var txtForPsswrd: TextView
    private lateinit var txtNewUser: TextView
    private lateinit var btnAcceder: Button
    private lateinit var edTxtUsuario: EditText
    private lateinit var edTxtPsswrd: EditText
    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private var usuarioActual = "abc123"

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edTxtUsuario = findViewById(R.id.main_loginUsername)
        edTxtPsswrd = findViewById(R.id.main_loginPassword)
        btnAcceder = findViewById(R.id.main_btnAcceder)
        txtForPsswrd = findViewById(R.id.main_forgotPassword)
        txtNewUser = findViewById(R.id.main_newuser)
        db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(this.applicationContext, "txt", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)

        edTxtUsuario.setText("jaax1")
        edTxtPsswrd.setText("j44x.EDSA")
    }

    override fun onStart() {
        super.onStart()
        val intent = intent.extras
        if( intent != null ){
            this.usuarioActual = intent.getString("usuarioActual")!!
            edTxtUsuario.setText( this.usuarioActual )
        }
    }

    override fun onResume() {
        super.onResume()
        btnAcceder.setOnClickListener {
            val usr = edTxtUsuario.text.toString()
            val pss = edTxtPsswrd.text.toString()
            val usuario = Usuario(usr, pss, "", ArrayList())
            val acceso = verificarLogin( usuario.nombre, usuario.password )

            if( acceso ){
                val intent = Intent(this.applicationContext, VerEmails::class.java)
                intent.putExtra("usuarioActual", usr)
                startActivity(intent)
                this.finish()
            } else {
                toast.setText("Usuario no encontrado")
                toast.show()
            }
        }

        txtNewUser.setOnClickListener {
            val intent = Intent(this.applicationContext, RegistrarUsuario::class.java)
            startActivity(intent)
            this.finish()
        }

        txtForPsswrd.setOnClickListener {
            val intent = Intent(this.applicationContext, ActualizarPssUsuario::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    private fun verificarLogin( usuario: String, psswrd: String ): Boolean{
        try {
            val cursor = db.getAllUsuarios()
            var i = 0
            if( usuario=="" || psswrd=="" ){
                toast.setText("Uno o más campos están vacíos")
                toast.show()
                return false
            } else {
                if( cursor.count>0 ){
                    val allUsers = arrayListOf<String>()
                    val allPsswrd = arrayListOf<String>()
                    while(cursor.moveToNext()){
                        allUsers.add(cursor.getString(0))
                        allPsswrd.add(cursor.getString(1))
                        if( allUsers.get(i).equals(usuario) && allPsswrd.get(i).equals(psswrd) ){
                            this.usuarioActual = usuario
                            return true
                        }
                        i++
                    }
                } else return false
            }
        } catch (excp: NullPointerException){}
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}