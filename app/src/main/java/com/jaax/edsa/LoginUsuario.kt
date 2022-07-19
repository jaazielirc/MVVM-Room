package com.jaax.edsa

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.jaax.edsa.data.model.User
import java.lang.NullPointerException

class LoginUsuario: AppCompatActivity() {
    private lateinit var txtForPsswrd: TextView
    private lateinit var btnAcceder: Button
    private lateinit var edTxtUsuario: EditText
    private lateinit var edTxtPsswrd: EditText
    private lateinit var db: DBHelper
    private lateinit var toast: Toast
    private var adview1: InterstitialAd? = null
    private lateinit var userActual: User

    companion object {
        private const val TAG_AD = "Ads"
        private const val ID_AD1 = "ca-app-pub-8133052963274724/3281408261"
    }

    private fun init() {
        val datosUsuario = this.intent.extras
        userActual = User(
            datosUsuario?.getString("usrNombre")!!,
            datosUsuario.getString("usrPassword")!!,
            datosUsuario.getString("usrKeyword")!!,
            ArrayList()
        )
        //val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        //notificationManager?.cancel(GestorNotificaciones.NOTIFICATION_ID)
    }

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_usuario)

        edTxtUsuario = findViewById(R.id.main_loginUsername)
        edTxtPsswrd = findViewById(R.id.main_loginPassword)
        btnAcceder = findViewById(R.id.main_btnAcceder)
        txtForPsswrd = findViewById(R.id.main_forgotPassword)
        db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(this.applicationContext, "txt", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        init()
        edTxtUsuario.setText(userActual.nombre)

        MobileAds.initialize(this@LoginUsuario) {
            val adRequest = AdRequest.Builder().build()
            createAd(adRequest)
        }
    }

    override fun onResume() {
        super.onResume()
        btnAcceder.setOnClickListener {
            val usr = edTxtUsuario.text.toString()
            val pss = edTxtPsswrd.text.toString()
            userActual.nombre = usr
            userActual.password = pss

            val acceso = verificarLogin( userActual.nombre, userActual.password )
            if( acceso ){
                if( adview1 != null ){
                    adview1?.show( this )
                } else {
                    Log.i( TAG_AD, "Ad is null" )
                }
            } else {
                toast.setText("Revisa tus datos")
                toast.show()
            }
        }

        txtForPsswrd.setOnClickListener {
            val intent = Intent(this.applicationContext, UpdateUsuario::class.java)
            intent.putExtra("Login_usrNombre", userActual.nombre)
            intent.putExtra("Login_usrPassword", userActual.password)
            intent.putExtra("Login_usrKeyword", userActual.keyword)
            startActivity(intent)
        }
    }

    private fun verificarLogin( usuario: String, psswrd: String ): Boolean{
        try {
            val cursor = db.getAllUsuarios()

            if( usuario=="" || psswrd=="" ){
                toast.setText("Uno o más campos están vacíos")
                toast.show()
                return false
            } else {
                if( cursor.count>0 ){ //debería existir sólo 1 usuario si ya se registró
                    while(cursor.moveToNext()){
                        //sólo tendrá un valor porque sólo se puede registrar un usuario por app
                        val usrYaRegistrado = cursor.getString(0)
                        val pssYaRegistrada = cursor.getString(1)

                        return (usrYaRegistrado==usuario && pssYaRegistrada==psswrd)
                    }
                } else return false
            }
        } catch (excp: NullPointerException){}
        return false
    }

    private fun createAd( adRequest: AdRequest ) {
        InterstitialAd.load(
            this,
            ID_AD1,
            adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    adview1 = null
                    Log.d( TAG_AD, "Ad not loaded" )
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    adview1 = p0
                    Log.d( TAG_AD, "Ad loaded" )
                    adview1?.fullScreenContentCallback = object : FullScreenContentCallback() {

                        override fun onAdFailedToShowFullScreenContent(p0: AdError?) {
                            Log.e( TAG_AD, "Ad failed" )
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.i( TAG_AD, "Ad showed" )
                            adview1 = null
                        }

                        override fun onAdDismissedFullScreenContent() {
                            Log.i( TAG_AD, "Ad dismissed" )
                            toast.setText("Bienvenid@")
                            toast.show()
                            edTxtPsswrd.setText("")
                            val intent = Intent(this@LoginUsuario, VerEmails::class.java)
                            intent.putExtra("Login_usrNombre", userActual.nombre)
                            intent.putExtra("Login_usrPassword", userActual.password)
                            intent.putExtra("Login_usrKeyword", userActual.keyword)
                            startActivity(intent)
                            this@LoginUsuario.finish()
                        }
                    }
                }
            })
    }
}