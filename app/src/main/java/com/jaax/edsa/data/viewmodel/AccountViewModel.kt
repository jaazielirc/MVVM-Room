package com.jaax.edsa.data.viewmodel

import androidx.lifecycle.*
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.model.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AccountViewModel(
    private val repository: RoomRepository,
    private val email: String
): ViewModel() {
    private var _accounts = MutableLiveData<List<Account>>(emptyList())
    val accounts: LiveData<List<Account>> get() = _accounts

    init {
        viewModelScope.launch {
            val accounts = repository.getAllAccountsByEmail(email)
            _accounts.value = accounts.accounts
        }
    }

    fun createAccount(type: String, password: String): Account {
        val lastId = runBlocking(Dispatchers.IO) { repository.getLastAccountId(email) } ?: 0
        return Account(lastId+1, type, password, email)
    }

    fun saveAccount(account: Account) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveAccount(account)
        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAccount(account)
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAccount(account)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class AccountViewModelFactory(private val repository: RoomRepository, private val email: String):
    ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return AccountViewModel(repository, email) as T
    }
}