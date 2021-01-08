package com.jaax.edsa.controlador

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jaax.edsa.modelo.DBHelper
import com.jaax.edsa.modelo.Usuario
import com.jaax.edsa.R
import com.jaax.edsa.vista.SupportNPss
import java.lang.NullPointerException

class ActualizarPssUsuario: AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var npsswrd: EditText
    private lateinit var ncpsswrd: EditText
    private lateinit var keyword: EditText
    private lateinit var btnUpdt: Button
    private lateinit var help: ImageView
    private lateinit var db: DBHelper
    private lateinit var toast: Toast

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.updt_pss_usuario)

        supportActionBar!!.title = "Cambiar contraseña"
        usuario = findViewById(R.id.usr_updt_username)
        npsswrd = findViewById(R.id.usr_updt_password)
        ncpsswrd = findViewById(R.id.usr_updt_npassword)
        keyword = findViewById(R.id.usr_updt_keyword)
        btnUpdt = findViewById(R.id.usr_updt_btnActualizar)
        help = findViewById(R.id.usr_updt_help)
        db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(this.applicationContext, "txt", Toast.LENGTH_LONG)
    }

    override fun onResume() {
        super.onResume()
        help.setOnClickListener { SupportNPss().show(supportFragmentManager, "helpNPss") }

        btnUpdt.setOnClickListener {
            val usr = usuario.text.toString()
            val npss = npsswrd.text.toString()
            val ncpss = ncpsswrd.text.toString()
            val key = keyword.text.toString()
            val usuario = Usuario(usr, npss, key, ArrayList())
            val acceso = datosValidos(usuario.nombre, usuario.password, ncpss, usuario.keyword)
            if( acceso ){
                val actualizar = actualizarPssUsuario(usuario)
                if( actualizar ) {
                    btnUpdt.isEnabled = false
                    btnUpdt.elevation = 5.0F
                    btnUpdt.setBackgroundResource(R.drawable.disable_btn_style)
                    btnUpdt.setTextColor(Color.GRAY)
                    val thread = object : Thread() {
                        override fun run() {
                            try {
                                sleep(3000)
                            } finally {
                                val intent = Intent(applicationContext, LoginUsuario::class.java)
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

    private fun actualizarPssUsuario( user: Usuario ): Boolean {
        val cursor = db.getAllUsuarios()
        val listUsrs = arrayListOf<String>()
        val listKeys = arrayListOf<String>()
        var i = 0
        var update = false
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        try {
            if( cursor.count>0 ){
                while( cursor.moveToNext() ){
                    listUsrs.add(cursor.getString(0))
                    listKeys.add(cursor.getString(2))
                    if( listUsrs[i]==user.nombre && listKeys[i]==user.keyword ){
                        update = db.updtDatosUsuario(user.nombre, user.password, user.keyword)
                        break
                    }
                    i++
                }
            }
            if(update){
                toast.setText("Tu contraseña se ha actualizado\nRedirigiendo al inicio...")
                toast.show()
            } else {
                toast.setText("Error al actualizar")
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 300)
                toast.show()
            }
        }catch(ne: NullPointerException){}
        return update
    }

    private fun datosValidos(name: String, npss: String, ncpss: String, key: String): Boolean{
        val counts = arrayOf(0, 0, 0, 0, 0)
        val regexU = Regex("(?=.*[a-zA-Z])(?=.*\\d)\\S{5,15}" )
        val regexP = Regex("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[,._*%-])\\S{8,15}")
        val regexC = Regex("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[,._*%-])\\S{8,15}")
        val regexK = Regex("(?=.*[a-zA-Z])(?=.*[,._*%-])\\S{6,15}")
        toast.setText("Si necesitas ayuda, toca '?' para más información")

        if( name=="" || npss=="" || ncpss=="" || key=="" ){
            toast.setText("Uno o más campos están vacíos, si necesitas ayuda toca '?'")
            toast.show()
            return false
        } else {
            if(!name.matches(regexU)){
                counts[0]++
                usuario.error = "!"
                toast.show()
            }
            if(!npss.matches(regexP)){
                counts[1]++
                npsswrd.error = "!"
                toast.show()
            }
            if(!ncpss.matches(regexC)){
                counts[2]++
                ncpsswrd.error = "!"
                toast.show()
            }
            if(!key.matches(regexK)){
                counts[3]++
                keyword.error = "!"
                toast.show()
            }

            if( npss != ncpss ){
                counts[4]++
                ncpsswrd.error = "Las contraseñas no coinciden"
            }
        }
        if( counts[0]==0 && counts[1]==0 && counts[2]==0 && counts[3]==0 && counts[4]==0 )
            return true
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this.applicationContext, LoginUsuario::class.java)
        startActivity(intent)
        this.finish()
    }
}