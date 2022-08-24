package com.jaax.edsa.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.jaax.edsa.R
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.viewmodel.UserViewModel
import com.jaax.edsa.data.viewmodel.UserViewModelFactory
import com.jaax.edsa.ui.fragments.AddUserFragment
import com.jaax.edsa.ui.fragments.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    @Inject lateinit var repository: RoomRepository
    @Inject lateinit var factory: UserViewModelFactory
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        setNextFragment()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun setNextFragment() {
        val user = viewModel.getUser()
        if(user == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrame, AddUserFragment())
                .addToBackStack("adduser")
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrame, LoginFragment())
                .addToBackStack("login")
                .commit()
        }
    }
}