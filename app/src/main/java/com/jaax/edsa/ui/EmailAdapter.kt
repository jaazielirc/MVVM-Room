package com.jaax.edsa.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.jaax.edsa.R
import com.jaax.edsa.data.model.Email
import com.jaax.edsa.databinding.DataEmailAdapterBinding

class EmailAdapter(
    private var listEmails: List<Email>,
    private val onShowListener: (String) -> Unit,
    private val onEditListener: () -> Unit,
    private val onUpdateListener: (String, Email, Int) -> Unit,
    private val onDeleteListener: (Email, Int) -> Unit
): RecyclerView.Adapter<EmailAdapter.EmailHolder>() {

    inner class EmailHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = DataEmailAdapterBinding.bind(view)

        fun bind(
            email: Email,
            onShowListener: (String) -> Unit,
            onEditListener: () -> Unit,
            onUpdateListener: (String, Email, Int) -> Unit,
            onDeleteListener: (Email, Int) -> Unit
        ) {
            var oldAddress = ""
            binding.tvEmail.setText(email.address)
            binding.tvPassword.setText(email.password)
            binding.imgBtnShowAccounts.setOnClickListener { onShowListener(email.address) }
            binding.imgBtnEditEmail.setOnClickListener {
                oldAddress = binding.tvEmail.text.toString()
                binding.tvEmail.isEnabled = true
                binding.tvPassword.isEnabled = true
                binding.imgBtnUpdateEmail.visibility = View.VISIBLE
                binding.imgBtnEditEmail.visibility = View.INVISIBLE
                binding.imgBtnShowAccounts.visibility = View.INVISIBLE
                binding.imgBtnDeleteEmail.visibility = View.INVISIBLE
                onEditListener()
            }
            binding.imgBtnUpdateEmail.setOnClickListener {
                email.address = binding.tvEmail.text.toString()
                email.password = binding.tvPassword.text.toString()
                binding.tvEmail.isEnabled = false
                binding.tvPassword.isEnabled = false
                binding.imgBtnUpdateEmail.visibility = View.INVISIBLE
                binding.imgBtnEditEmail.visibility = View.VISIBLE
                binding.imgBtnShowAccounts.visibility = View.VISIBLE
                binding.imgBtnDeleteEmail.visibility = View.VISIBLE
                onUpdateListener(oldAddress, email, adapterPosition)
            }
            binding.imgBtnDeleteEmail.setOnClickListener { onDeleteListener(email, adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.data_email_adapter, parent, false)
        return EmailHolder(view)
    }

    override fun onBindViewHolder(holder: EmailHolder, position: Int) {
        val email = listEmails[position]
        holder.bind(email, onShowListener, onEditListener, onUpdateListener, onDeleteListener)
    }

    override fun getItemCount(): Int {
        return listEmails.size
    }
}