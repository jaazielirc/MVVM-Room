package com.jaax.edsa.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.jaax.edsa.databinding.FragmentAdduserBinding
import com.jaax.edsa.mvp.AddUserMVP
import com.jaax.edsa.presenter.AddUserPresenter

class AddUser: Fragment(), AddUserMVP.View {
    private lateinit var presenter: AddUserMVP.Presenter
    private var _bind: FragmentAdduserBinding? = null
    private val bind get() = _bind!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentAdduserBinding.inflate(inflater, container, false)
        presenter = AddUserPresenter()
        presenter.setView(this)
        return bind.root
    }

    override fun onResume() {
        super.onResume()
        bind.btnSaveAdduser.setOnClickListener {
            if( presenter.btnSaveClicked() ){
                //cambiar a loginfragment
            }
        }
    }

    override fun getUsername(): String {
        presenter.isValidUsername()
        return bind.tieAdduserUser.text.toString().trim()
    }

    override fun getPassword(): String {
        return bind.tieAdduserPassword.text.toString().trim()
    }

    override fun getKeyword(): String {
        return bind.tieAdduserKeyword.text.toString().trim()
    }

    override fun showSuccessAddUser() {
        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
    }

    override fun showErrorAddUserUsername() {
        bind.tieAdduserUser.error = "Al menos 3 caracteres"
    }

    override fun showErrorAddUserPassword() {
        bind.tieAdduserPassword.error = "Al menos 5 caracteres"
    }

    override fun showErrorAddUserKeyword() {
        bind.tieAdduserKeyword.error = "Al menos 4 caracteres"
    }
}