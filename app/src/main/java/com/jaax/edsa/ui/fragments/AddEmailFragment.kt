package com.jaax.edsa.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.model.Email
import com.jaax.edsa.data.viewmodel.EmailViewModel
import com.jaax.edsa.data.viewmodel.EmailViewModelFactory
import com.jaax.edsa.databinding.FragmentAddEmailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ClassCastException
import javax.inject.Inject

@AndroidEntryPoint
class AddEmailFragment @Inject constructor(private val username: String): DialogFragment() {
    private var _binding: FragmentAddEmailBinding? = null
    private val binding get() = _binding!!
    private var callback: NotifyEmailAddedListener? = null

    @Inject lateinit var repository: RoomRepository
    @Inject lateinit var factory: EmailViewModelFactory
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