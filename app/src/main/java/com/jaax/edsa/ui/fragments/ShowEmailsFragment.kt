package com.jaax.edsa.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.db.DatabaseEDSA
import com.jaax.edsa.data.model.Email
import com.jaax.edsa.data.viewmodel.EmailViewModel
import com.jaax.edsa.data.viewmodel.EmailViewModelFactory
import com.jaax.edsa.databinding.FragmentShowEmailsBinding
import com.jaax.edsa.ui.EmailAdapter

class ShowEmailsFragment(private val username: String): Fragment(),
    AddEmailFragment.NotifyEmailAddedListener {

    private var emailMutableList: MutableList<Email> = mutableListOf()
    private var _binding: FragmentShowEmailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: RoomRepository
    private lateinit var factory: EmailViewModelFactory
    private lateinit var viewModel: EmailViewModel
    private lateinit var adapter: EmailAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = RoomRepository(DatabaseEDSA(requireActivity().applicationContext))
        factory = EmailViewModelFactory(repository, username)
        viewModel = ViewModelProvider(this, factory)[EmailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowEmailsBinding.inflate(inflater, container, false)
        adapter = EmailAdapter(
            emailMutableList,
            onShowListener = { emailAddress -> showAccounts(emailAddress) },
            onEditListener = {  },
            onUpdateListener = { oldAddress, email, position -> updateEmail(oldAddress, email, position) },
            onDeleteListener = { email, position -> deleteEmail(email, position) }
        )
        layoutManager = LinearLayoutManager(requireActivity().applicationContext)

        binding.recyclerEmails.setHasFixedSize(true)
        binding.recyclerEmails.layoutManager = layoutManager
        binding.recyclerEmails.adapter = adapter

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.emails.observe(viewLifecycleOwner) { emails ->
            emailMutableList.addAll(emails)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.btnAddEmail.setOnClickListener { addEmail() }
    }

    @Suppress("DEPRECATION")
    private fun addEmail() {
        val addEmailFragment = AddEmailFragment(username)
        addEmailFragment.setTargetFragment(this, 0)
        addEmailFragment.show(parentFragmentManager, "addemail")
    }

    private fun showAccounts(emailAddress: String) {
        ShowAccountsFragment(emailAddress).show(parentFragmentManager, "showaaccounts")
    }

    private fun updateEmail(oldAddress: String, email: Email, position: Int) {
        viewModel.updateEmail(oldAddress, email)
        viewModel.updateAccountsByEmail(oldAddress, email.address)
        adapter.notifyItemChanged(position)
    }

    private fun deleteEmail(email: Email, position: Int) {
        viewModel.dropAccountsByEmail(email.address)
        viewModel.deleteEmail(email)
        emailMutableList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    override fun notify(email: Email) {
        emailMutableList.add(email)
        adapter.notifyItemInserted(adapter.itemCount)
    }
}