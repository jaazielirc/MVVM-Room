package com.jaax.edsa.vista

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

class VerEmails: AppCompatActivity(),
    AddMailFragment.OnCallbackReceivedAdd,
    UpdateMailFragment.OnCallbackReceivedEdit,
    DeleteMailFragment.OnCallbackReceivedDel {

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

    private var usuarioActual = "abc123"

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
        supportActionBar?.title = "EDSA: $usuarioActual"
        prevPass = ArrayList(0)
    }

    override fun onStart() {
        super.onStart()
        val intent = intent.extras
        if( intent != null ) usuarioActual = intent.getString("usuarioActual")!!
        mostrarEmails()
    }

    override fun onResume() {
        super.onResume()
        onClick()
    }

    private fun onClick(){
        addEmail.setOnClickListener {
            AddMailFragment(usuarioActual).show(
                supportFragmentManager,
                "agregarEmailNuevo"
            )}
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
                            VerCuentas(emailAdapter.emails[pos].nombre).show(this@VerEmails.supportFragmentManager, "verCuentas")
                        }
                        R.id.menu_editmail -> {
                            val updt = UpdateMailFragment(usuarioActual)
                            val bundle = Bundle()
                            val bundleNombre = emailAdapter.emails[pos].nombre
                            val bundlePsswrd = emailAdapter.emails[pos].passwrd
                            updt.arguments = bundle
                            bundle.putString("bundleEmailNombre", bundleNombre)
                            bundle.putString("bundleEmailPsswrd", bundlePsswrd)
                            updt.show(this@VerEmails.supportFragmentManager, "updateEmail")
                        }
                        R.id.menu_delemail -> {
                            DeleteMailFragment(
                                emailAdapter.emails[pos].ID,
                                emailAdapter.emails[pos].nombre
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
        try {
            val cursor = db.getEmailsById(this.usuarioActual)
            if( cursor.count>0 ){
                while(cursor.moveToNext()){
                    val email = Email(
                        this.usuarioActual,
                        cursor.getString(1),
                        cursor.getString(2),
                        ArrayList()
                    )
                    allEmails.add(email)
                }
                txtNoEmail.visibility = View.GONE
                imgNoEmail.visibility = View.GONE
            }
            emailAdapter = EmailAdapter(this.applicationContext, allEmails)
            listaEmail.adapter = emailAdapter
            hideAndRefillPasswords(prevPass, emailAdapter, true)
        }catch (sql: SQLiteException){}
    }

    private fun refreshListEmails(){
        val allEmails = ArrayList<Email>()
        try {
            val cursor = db.getEmailsById(this.usuarioActual)
            if( cursor.count>0 ){
                while(cursor.moveToNext()){
                    val email = Email(
                        this.usuarioActual,
                        cursor.getString(1),
                        cursor.getString(2),
                        ArrayList()
                    )
                    allEmails.add(email)
                }
                txtNoEmail.visibility = View.GONE
                imgNoEmail.visibility = View.GONE
            }
            emailAdapter.emails.clear()
            emailAdapter.emails.addAll(allEmails)
            switchView.isChecked = false
            hideAndRefillPasswords(prevPass, emailAdapter, true)
        }catch (sql: SQLiteException){}
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
                emailAdapter.notifyDataSetChanged()
                return true
            }
        })

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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this.applicationContext, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}