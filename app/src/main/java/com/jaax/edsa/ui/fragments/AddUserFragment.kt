package com.jaax.edsa.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.jaax.edsa.R
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.viewmodel.UserViewModel
import com.jaax.edsa.data.viewmodel.UserViewModelFactory
import com.jaax.edsa.databinding.FragmentAddUserBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddUserFragment: Fragment() {
    private var _binding: FragmentAddUserBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var repository: RoomRepository
    @Inject lateinit var factory: UserViewModelFactory
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUserBinding.inflate(inflater, container, false)
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
        binding.btnSaveUser.setOnClickListener {
            val user = viewModel.createUser(
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString(),
                binding.etKeyword.text.toString()
            )
            if(viewModel.isValidUsername(user.name)) {
                viewModel.saveUser(user)
                Toast.makeText(
                    requireActivity().applicationContext,
                    "User saved",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().supportFragmentManager
                    .popBackStack(
                        "adduser",
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainFrame, LoginFragment())
                    .addToBackStack("login")
                    .commit()
            }
        }
    }
}