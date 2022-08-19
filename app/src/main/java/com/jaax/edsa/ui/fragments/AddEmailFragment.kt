package com.jaax.edsa.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.db.DatabaseEDSA
import com.jaax.edsa.data.model.Email
import com.jaax.edsa.data.viewmodel.EmailViewModel
import com.jaax.edsa.data.viewmodel.EmailViewModelFactory
import com.jaax.edsa.databinding.FragmentAddEmailBinding
import java.lang.ClassCastException

class AddEmailFragment(private val username: String): DialogFragment() {
    private var _binding: FragmentAddEmailBinding? = null
    private val binding get() = _binding!!
    private var callback: NotifyEmailAddedListener? = null
    private lateinit var repository: RoomRepository
    private lateinit var factory: EmailViewModelFactory
    private lateinit var viewModel: EmailViewModel

    interface NotifyEmailAddedListener {
        fun notify(email: Email)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = targetFragment as? NotifyEmailAddedListener
        } catch(cce: ClassCastException) {
            cce.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = RoomRepository(DatabaseEDSA(requireActivity().applicationContext))
        factory = EmailViewModelFactory(repository, username)
        viewModel = ViewModelProvider(this, factory)[EmailViewModel::class.java]
    }

    @SuppressLint("ShowToast")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentAddEmailBinding.inflate(requireActivity().layoutInflater)

        return AlertDialog.Builder(activity)
            .setView(binding.root)
            .create()
    }

    override fun onResume() {
        super.onResume()
        binding.btnSaveEmail.setOnClickListener {
            val email = viewModel.createEmail(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                username
            )
            viewModel.saveEmail(email)
            callback?.notify(email)
            this.dismiss()
        }
    }
}