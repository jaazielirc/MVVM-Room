package com.jaax.edsa.Vista

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jaax.edsa.Controlador.EmailAdapter
import com.jaax.edsa.Controlador.MainActivity
import com.jaax.edsa.Modelo.DBHelper
import com.jaax.edsa.Modelo.Email
import com.jaax.edsa.R
import java.sql.SQLException

class VerEmails: AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var addEmail: FloatingActionButton
    private lateinit var refreshList: FloatingActionButton
    private lateinit var listaEmail: ListView
    private lateinit var txtNoEmail: TextView
    private lateinit var imgNoEmail: ImageView
    private lateinit var toast: Toast
    private lateinit var emailAdapter: EmailAdapter

    private var usuarioActual = "abc123"

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_emails)

        supportActionBar!!.title = "Emails"
        addEmail = findViewById(R.id.emails_add)
        listaEmail = findViewById(R.id.emails_lista)
        txtNoEmail = findViewById(R.id.textNoEmail)
        imgNoEmail = findViewById(R.id.imgNoEmail)
        refreshList = findViewById(R.id.emails_refresh)
        db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(this.applicationContext, "txt", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
    }

    override fun onStart() {
        super.onStart()
        val intent = intent.extras
        if( intent != null ) usuarioActual = intent.getString("usuarioActual")!!
        mostrarEmails()
    }

    override fun onResume() {
        super.onResume()
        addEmail.setOnClickListener { AddMailFragment(usuarioActual).show(supportFragmentManager, "agregarEmailNuevo") }
        refreshList.setOnClickListener {
            refreshList.visibility = View.INVISIBLE
            toast.setText("Espere...")
            toast.setGravity(Gravity.BOTTOM, 0, 0)
            toast.show()
            refreshList.postDelayed(object : Runnable{
                override fun run() {
                    refreshList.visibility = View.VISIBLE
                }
            }, 3000)
            refreshListEmails()
        }
    }

    private fun refreshListEmails(){
        val allEmails = ArrayList<Email>()

        try {
            val cursor = db.getEmailsByID(this.usuarioActual)
            if( cursor.count>0 ){
                while(cursor.moveToNext()){
                    val email = Email(this.usuarioActual, cursor.getString(1), cursor.getString(2), ArrayList())
                    allEmails.add(email)
                }
                txtNoEmail.visibility = View.GONE
                imgNoEmail.visibility = View.GONE
            }
            emailAdapter.getData().clear()
            emailAdapter.getData().addAll(allEmails)
            emailAdapter.notifyDataSetChanged()
        }catch(sql: SQLException){}
    }

    private fun mostrarEmails() {
        val allEmails = ArrayList<Email>()

        try {
            val cursor = db.getEmailsByID(this.usuarioActual)
            if( cursor.count>0 ){
                while(cursor.moveToNext()){
                    val email = Email(this.usuarioActual, cursor.getString(1), cursor.getString(2), ArrayList())
                    allEmails.add(email)
                }
                txtNoEmail.visibility = View.GONE
                imgNoEmail.visibility = View.GONE
            }
            emailAdapter = EmailAdapter(this.applicationContext, allEmails)
            listaEmail.adapter = emailAdapter
        }catch(sql: SQLException){}
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this.applicationContext, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}