package com.jaax.edsa.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.model.Email
import com.jaax.edsa.data.model.User
import com.jaax.edsa.data.viewmodel.EmailViewModel
import com.jaax.edsa.data.viewmodel.EmailViewModelFactory
import com.jaax.edsa.databinding.FragmentDeleteEmailBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeleteEmailFragment(private val email: Email, private val user: User): DialogFragment() {
    private var _binding: FragmentDeleteEmailBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var repository: RoomRepository
    @Inject lateinit var factory: EmailViewModelFactory
    private lateinit var viewModel: EmailViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDeleteEmailBinding.inflate(requireActivity().layoutInflater)
        return AlertDialog.Builder(activity)
            .setView(binding.root)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory)[EmailViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        binding.btnDeleteEmail.setOnClickListener {
            val keyword = binding.etKeyword.text.toString()
            if(keyword == user.keyword) {
                viewModel.dropAccountsByEmail(email.address)
                viewModel.deleteEmail(email)
                this@DeleteEmailFragment.dismiss()
            }
        }
    }
}