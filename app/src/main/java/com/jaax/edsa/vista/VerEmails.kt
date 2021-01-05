package com.jaax.edsa.vista

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jaax.edsa.controlador.*
import com.jaax.edsa.modelo.DBHelper
import com.jaax.edsa.modelo.Email
import com.jaax.edsa.R
import java.sql.SQLException

class VerEmails: AppCompatActivity(),
    SearchView.OnQueryTextListener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener,
    AddMailFragment.OnCallbackReceivedAdd,
    UpdateMailFragment.OnCallbackReceivedEdit,
    DeleteMailFragment.OnCallbackReceivedDel {

    private lateinit var db: DBHelper
    private lateinit var addEmail: FloatingActionButton
    private lateinit var refreshList: FloatingActionButton
    private lateinit var listaEmail: ListView
    private lateinit var txtNoEmail: TextView
    private lateinit var imgNoEmail: ImageView
    private lateinit var toast: Toast
    private lateinit var emailAdapter: EmailAdapter
    private lateinit var searchView: androidx.appcompat.widget.SearchView

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
        searchView = findViewById(R.id.emails_search)
        db = DBHelper(this.applicationContext, DBHelper.nombreDB, null, DBHelper.version)
        toast = Toast.makeText(this.applicationContext, "txt", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        searchView.setOnQueryTextListener(this)
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
        refreshList.setOnClickListener { refreshListEmails() }
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

    private fun refreshListEmails(){
        refreshList.visibility = View.INVISIBLE
        searchView.setQuery("", false)
        refreshList.postDelayed({ refreshList.visibility = View.VISIBLE }, 3000)
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
                searchView.visibility = View.VISIBLE
            } else {
                searchView.visibility = View.INVISIBLE
            }
            emailAdapter.emails.clear()
            emailAdapter.emails.addAll(allEmails)
            emailAdapter.notifyDataSetChanged()
        }catch (sql: SQLException){}
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
            } else {
                searchView.visibility = View.INVISIBLE
            }
            emailAdapter = EmailAdapter(this.applicationContext, allEmails)
            listaEmail.adapter = emailAdapter
        }catch (sql: SQLException){}
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this.applicationContext, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    override fun onQueryTextSubmit(p0: String?): Boolean { return false }

    override fun onQueryTextChange(p0: String?): Boolean {
        emailAdapter.getFilter().filter(p0)
        return false
    }

    override fun refreshByAdding() { refreshListEmails() }
    override fun refreshByEditing() { refreshListEmails() }
    override fun refreshByDeleting() { refreshListEmails() }
}