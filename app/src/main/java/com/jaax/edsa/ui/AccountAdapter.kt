package com.jaax.edsa.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaax.edsa.R
import com.jaax.edsa.data.model.Account
import com.jaax.edsa.databinding.DataAccountAdapterBinding

class AccountAdapter(
    private val listAccounts: List<Account>,
    private val onEditListener: () -> Unit,
    private val onUpdateListener: (Account, Int) -> Unit,
    private val onDeleteListener: (Account, Int) -> Unit
):
    RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = DataAccountAdapterBinding.bind(view)

        fun bind(
            account: Account,
            onEditListener: () -> Unit,
            onUpdateListener: (Account, Int) -> Unit,
            onDeleteListener: (Account, Int) -> Unit
        ) {
            binding.tvType.setText(account.type)
            binding.tvPassword.setText(account.password)

            binding.imgBtnEditAccount.setOnClickListener {
                binding.tvType.isEnabled = true
                binding.tvPassword.isEnabled = true
                binding.imgBtnUpdateAccount.visibility = View.VISIBLE
                binding.imgBtnEditAccount.visibility = View.INVISIBLE
                binding.imgBtnDeleteAccount.visibility = View.INVISIBLE
                onEditListener()
            }
            binding.imgBtnUpdateAccount.setOnClickListener {
                account.type = binding.tvType.text.toString()
                account.password = binding.tvPassword.text.toString()
                binding.tvType.isEnabled = false
                binding.tvPassword.isEnabled = false
                binding.imgBtnUpdateAccount.visibility = View.INVISIBLE
                binding.imgBtnEditAccount.visibility = View.VISIBLE
                binding.imgBtnDeleteAccount.visibility = View.VISIBLE
                onUpdateListener(account, adapterPosition)
            }
            binding.imgBtnDeleteAccount.setOnClickListener { onDeleteListener(account, adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.data_account_adapter, parent, false)

        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = listAccounts[position]
        holder.bind(account, onEditListener, onUpdateListener, onDeleteListener)
    }

    override fun getItemCount(): Int {
        return listAccounts.size
    }
}