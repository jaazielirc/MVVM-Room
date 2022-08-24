package com.jaax.edsa.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.viewmodel.UserViewModel
import com.jaax.edsa.data.viewmodel.UserViewModelFactory
import com.jaax.edsa.databinding.FragmentUpdateUserBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UpdateUserFragment: Fragment() {
    private var _binding: FragmentUpdateUserBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var repository: RoomRepository
    @Inject lateinit var factory: UserViewModelFactory
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateUserBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.btnUpdateUser.setOnClickListener {
            lifecycleScope.launch {
                val user = viewModel.createUser(
                    binding.etUpdateUsername.text.toString(),
                    binding.etUpdatePassword.text.toString(),
                    binding.etUpdateKeyword.text.toString()
                )
                if( viewModel.existUser(user.name) ){
                    if(viewModel.isValidUsername(user.name)
                        && (viewModel.isValidPassword(user.password))
                        && (viewModel.isValidKeyword(user.keyword))
                    ) {
                        viewModel.updateUser(user)
                        Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack("updateuser", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    } else {
                        if(viewModel.isValidUsername(user.name)) {
                            binding.etUpdateUsername.error = "4 a 10 caracteres a-z sin espacios"
                        }
                        if(viewModel.isValidPassword(user.password)) {
                            binding.etUpdatePassword.error = "6 a 20 caracteres"
                        }
                        if(viewModel.isValidKeyword(user.keyword)) {
                            binding.etUpdateKeyword.error = "4 a 15 caracteres sin espacios"
                        }
                    }
                } else {
                    Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    binding.btnUpdateUser.visibility  = View.INVISIBLE
                    delay(2000)
                    binding.btnUpdateUser.visibility  = View.VISIBLE
                }
            }
        }
    }
}