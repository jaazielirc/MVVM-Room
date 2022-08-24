package com.jaax.edsa.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jaax.edsa.R
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.viewmodel.UserViewModel
import com.jaax.edsa.data.viewmodel.UserViewModelFactory
import com.jaax.edsa.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var repository: RoomRepository
    @Inject lateinit var factory: UserViewModelFactory
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.username.observe(viewLifecycleOwner) { username ->
            binding.etUsername.setText(username)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
                val user = viewModel.createUser(
                    binding.etUsername.text.toString(),
                    binding.etPassword.text.toString(),
                    "",
                )
                if(viewModel.isValidUser(user.name, user.password)) {
                    fragmentTransition(ShowEmailsFragment(user.name), "showemails")
                } else {
                    Toast.makeText(context, "Revisa tus credenciales", Toast.LENGTH_SHORT).show()
                    binding.btnLogin.visibility = View.INVISIBLE
                    delay(2500)
                    binding.btnLogin.visibility = View.VISIBLE
                }
            }
        }

        binding.tvForgotData.setOnClickListener {
            fragmentTransition(UpdateUserFragment(), "updateuser")
        }
    }

    private fun fragmentTransition(fragment: Fragment, tag: String) {
        binding.etPassword.setText("")
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrame, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }
}