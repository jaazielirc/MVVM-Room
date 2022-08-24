package com.jaax.edsa.data.viewmodel

import androidx.lifecycle.*
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.model.Email
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailViewModel @Inject constructor(
    private val repository: RoomRepository,
    private val username: String
): ViewModel() {

    private var _emails = MutableLiveData<List<Email>>(emptyList())
    val emails: LiveData<List<Email>> get() = _emails

    init {
        viewModelScope.launch {
            val emails = repository.getAllEmails(username)
            _emails.value = emails.emails
        }
    }

    fun createEmail(email: String, password: String, username: String): Email {
        return Email(email, password, username)
    }

    fun saveEmail(email: Email) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveEmail(email)
        }
    }

    fun updateEmail(oldAddress: String, email: Email) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEmail(oldAddress, email)
        }
    }

    fun deleteEmail(email: Email) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dropAccountsByEmail(email.address)
            repository.deleteEmail(email)
        }
    }

    fun dropAccountsByEmail(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dropAccountsByEmail(address)
        }
    }

    fun updateAccountsByEmail(oldEmailAddress: String, emailAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAccountsByEmail(oldEmailAddress, emailAddress)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class EmailViewModelFactory(private val repository: RoomRepository, private val username: String):
    ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return EmailViewModel(repository, username) as T
    }
}