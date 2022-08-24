package com.jaax.edsa.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.model.Account
import com.jaax.edsa.data.viewmodel.AccountViewModel
import com.jaax.edsa.data.viewmodel.AccountViewModelFactory
import com.jaax.edsa.databinding.FragmentShowAccountsBinding
import com.jaax.edsa.ui.AccountAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShowAccountsFragment(private val email: String): DialogFragment() {

    private var accountMutableList: MutableList<Account> = mutableListOf()
    private var _binding: FragmentShowAccountsBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var repository: RoomRepository
    private lateinit var factory: AccountViewModelFactory
    private lateinit var viewModel: AccountViewModel
    private lateinit var adapter: AccountAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory = AccountViewModelFactory(repository, email)
        viewModel = ViewModelProvider(this, factory)[AccountViewModel::class.java]
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentShowAccountsBinding.inflate(requireActivity().layoutInflater)
        initRecyclerView()

        viewModel.accounts.observe(this) { accounts ->
            accountMutableList.addAll(accounts)
            adapter.notifyDataSetChanged()
        }
        binding.toolbarAccounts.title = email

        return AlertDialog.Builder(requireActivity()).setView(binding.root).create()
    }

    override fun onResume() {
        super.onResume()
        binding.btnAddAccount.setOnClickListener { addAccount() }
    }

    private fun addAccount() {
        val account = viewModel.createAccount("", "")
        viewModel.saveAccount(account)
        accountMutableList.add(account)
        adapter.notifyItemInserted(adapter.itemCount)
    }

    private fun updateAccount(account: Account, position: Int) {
        viewModel.updateAccount(account)
        adapter.notifyItemChanged(position)
    }

    private fun deleteAccount(account: Account, position: Int) {
        viewModel.deleteAccount(account)
        accountMutableList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    private fun initRecyclerView() {
        adapter = AccountAdapter(
            accountMutableList,
            onEditListener = { },
            onUpdateListener = { account, position -> updateAccount(account, position) },
            onDeleteListener = { account, position -> deleteAccount(account, position) })
        layoutManager = LinearLayoutManager(requireActivity().applicationContext)

        binding.recyclerAccounts.setHasFixedSize(true)
        binding.recyclerAccounts.layoutManager = layoutManager
        binding.recyclerAccounts.adapter = adapter
    }
}