package com.jaax.edsa.controlador

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jaax.edsa.controlador.*
import com.jaax.edsa.modelo.DBHelper
import com.jaax.edsa.modelo.Email
import com.jaax.edsa.R
import com.jaax.edsa.modelo.Cuenta
import com.jaax.edsa.modelo.Usuario
import com.jaax.edsa.vista.NotificationService

class VerEmails: AppCompatActivity(),
    AddMailFragment.OnCallbackReceivedAdd,
    UpdateMailFragment.OnCallbackReceivedEdit,
    DeleteMailFragment.OnCallbackReceivedDel,
    VerCuentas.OnCallbackReceivedView {

    private lateinit var db: DBHelper
    private lateinit var addEmail: FloatingActionButton
    private lateinit var listaEmail: ListView
    private lateinit var txtNoEmail: TextView
    private lateinit var imgNoEmail: ImageView
    private lateinit var toast: Toast
    private lateinit var emailAdapter: EmailAdapter
    private lateinit var prevPass: ArrayList<String>
    private lateinit var switchView: SwitchCompat
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var usuarioActual: Usuario

    private fun initUsuarioLogueado(){
        val datosUsuario = this.intent.extras
        usuarioActual = Usuario(
            datosUsuario?.getString("Login_usrNombre")!!,
            datosUsuario.getString("Login_usrPassword")!!,
            datosUsuario.getString("Login_usrKeyword")!!,
            ArrayList() //en mostrar emails se actualizan este valor
        )
    }

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_emails)

        addEmail = findViewById(R.id.emails_add)
        listaEmail = findViewById(R.id.emails_lista)
        txtNoEmail = findViewById(R.id.textNoEmail)
        imgNoEmail = findViewById(R.id.imgNoEmail)
        swipeRefresh = findViewById(R.id.emails_refresh)
        db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(this.applicationContext, "txt", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        prevPass = ArrayList(0)

        initUsuarioLogueado()
    }

    override fun onStart() {
        super.onStart()
        mostrarEmails()
        supportActionBar?.title = "Emails de: ${this.usuarioActual.nombre}"
    }

    override fun onResume() {
        super.onResume()
        onClick()
    }

    private fun onClick(){
        addEmail.setOnClickListener {
            addEmail.visibility = View.INVISIBLE
            AddMailFragment(usuarioActual).show(
                supportFragmentManager,
                "agregarEmailNuevo"
            )
        }
        swipeRefresh.setOnRefreshListener {
            refreshListEmails()
            swipeRefresh.isRefreshing = false
        }
        listaEmail.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, view, pos, _ ->
                view?.isSelected = true
                val popupMenu = PopupMenu(this@VerEmails, view)
                popupMenu.menuInflater.inflate(R.menu.opc_email, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.menu_cuentas -> {

                            VerCuentas(usuarioActual.emails[pos]).show(this@VerEmails.supportFragmentManager, "verCuentas")
                        }
                        R.id.menu_editmail -> {
                            addEmail.visibility = View.INVISIBLE
                            val updt = UpdateMailFragment(usuarioActual.emails[pos], usuarioActual)
                            val bundle = Bundle()
                            val bundleNombre = usuarioActual.emails[pos].nombre
                            val bundlePsswrd = usuarioActual.emails[pos].passwrd
                            updt.arguments = bundle
                            bundle.putString("bundleEmailNombre", bundleNombre)
                            bundle.putString("bundleEmailPsswrd", bundlePsswrd)
                            updt.show(this@VerEmails.supportFragmentManager, "updateEmail")
                        }
                        R.id.menu_delemail -> {
                            addEmail.visibility = View.INVISIBLE
                            DeleteMailFragment(
                                usuarioActual.emails[pos],
                                usuarioActual
                            ).show(this@VerEmails.supportFragmentManager, "deleteEmail")
                        }
                    }
                    true
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    popupMenu.gravity = Gravity.CENTER_HORIZONTAL
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true)
                }
                popupMenu.show()
                true
            }
    }

    private fun mostrarEmails() {
        val allEmails = ArrayList<Email>()
        var i = 0
        try {
            val cursor = db.getEmailsById(usuarioActual.nombre)
            if( cursor.count>0 ){
                while(cursor.moveToNext()){
                    i++
                    val email = Email(
                        usuarioActual.nombre,
                        cursor.getString(1),
                        cursor.getString(2),
                        ArrayList()
                    )
                    allEmails.add(email)
                }
                usuarioActual.emails = setMissingDataEmail(allEmails) //se agregan las cuentas que es el dato faltante de los Email
                txtNoEmail.visibility = View.INVISIBLE
                imgNoEmail.visibility = View.INVISIBLE
            }
            emailAdapter = EmailAdapter(this.applicationContext, allEmails)
            listaEmail.adapter = emailAdapter
            hideAndRefillPasswords(prevPass, emailAdapter, true)
        }catch (sql: SQLiteException){}
    }

    private fun refreshListEmails(){
        addEmail.visibility = View.VISIBLE
        val allEmails = ArrayList<Email>()
        try {
            val cursor = db.getEmailsById(usuarioActual.nombre)
            var i = 0
            if( cursor.count>0 ){
                while(cursor.moveToNext()){
                    i++
                    val email = Email(
                        usuarioActual.nombre,
                        cursor.getString(1),
                        cursor.getString(2),
                        ArrayList()
                    )
                    allEmails.add(email)
                }
                usuarioActual.emails = setMissingDataEmail(allEmails)
                txtNoEmail.visibility = View.INVISIBLE
                imgNoEmail.visibility = View.INVISIBLE
            } else {
                txtNoEmail.visibility = View.VISIBLE
                imgNoEmail.visibility = View.VISIBLE
            }
            emailAdapter.emails.clear()
            emailAdapter.emails.addAll(allEmails)
            switchView.isChecked = false
            hideAndRefillPasswords(prevPass, emailAdapter, true)
        }catch (sql: SQLiteException){}
    }

    private fun setMissingDataEmail( allMails: ArrayList<Email> ): ArrayList<Email> {

        for( i: Int in 0 until allMails.size ){ //recorrer cada email
            val cursor = db.getCuentasById(allMails[i].nombre)
            val listaCuentas = ArrayList<Cuenta>()
            try { //no agrego 'if' xq si no tiene cuentas entonces no hay nada que cliquear
                while( cursor.moveToNext() ) {
                    val cuenta = Cuenta(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                    )
                    listaCuentas.add(cuenta)
                    allMails[i].cuentas = listaCuentas
                }
            } catch(sqli: SQLiteException){ sqli.toString() }
        }
        return allMails
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.opc_toolbar, menu)

        val searchView = menu?.findItem(R.id.menu_searchview)!!.actionView as SearchView
        switchView = menu.findItem(R.id.menu_view_password).actionView as SwitchCompat

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                emailAdapter.getFilter().filter(newText)
                refreshListEmails()
                emailAdapter.notifyDataSetChanged()
                return true
            }
        })

        searchView.setOnCloseListener {
            refreshListEmails()
            searchView.onActionViewCollapsed()
            true
        }

        switchView.setOnClickListener {
            if(switchView.isChecked){
                hideAndRefillPasswords(prevPass, emailAdapter, false)
            } else {
                hideAndRefillPasswords(prevPass, emailAdapter, true)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun hideAndRefillPasswords( arraypss: ArrayList<String>, adapter: EmailAdapter, hide: Boolean ) {
        val count = adapter.emails.count()
        for( i: Int in 0 until count ){ arraypss.add( adapter.emails[i].passwrd ) }
        if( hide ){
            for( i: Int in 0 until count ){
                adapter.emails[i].passwrd = "*******"
            }
            adapter.notifyDataSetChanged()
        } else {
            for( i: Int in 0 until count ){
                adapter.emails[i].passwrd = arraypss[i]
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun refreshByAdding() { refreshListEmails() }
    override fun refreshByEditing() { refreshListEmails() }
    override fun refreshByDeleting() { refreshListEmails() }
    override fun refreshByViewing() { refreshListEmails() }

    override fun onDestroy() {
        super.onDestroy()
        val service = Intent(this, NotificationService::class.java)
        startService(service)
    }
}