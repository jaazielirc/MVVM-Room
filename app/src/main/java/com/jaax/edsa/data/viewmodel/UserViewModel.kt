package com.jaax.edsa.data.viewmodel

import androidx.lifecycle.*
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.model.User
import com.jaax.edsa.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: RoomRepository): ViewModel() {
    private var _username = MutableLiveData("")
    val username: LiveData<String> get() = _username

    init {
        viewModelScope.launch {
            _username.value = repository.getUser()?.name
        }
    }

    fun createUser(name: String, password: String, keyword: String): User {
        return User(name, password, keyword)
    }

    fun saveUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveuser(user)
        }
    }

    fun getUser(): User? {
        return runBlocking(Dispatchers.IO) { repository.getUser() }
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dropEmailsByUser(user.name)
            repository.deleteUser(user)
        }
    }

    fun isValidUsername(username: String): Boolean {
        return username.matches(Utils.validateUsername)
    }

    fun isValidPassword(password: String): Boolean {
        return password.matches(Utils.validatePassword)
    }

    fun isValidKeyword(keyword: String): Boolean {
        return keyword.matches(Utils.validateKeyword)
    }

    fun existUser(username: String): Boolean {
        val userdb = runBlocking { repository.getUser() }
        return username == userdb?.name
    }
}

@Suppress("UNCHECKED_CAST")
class UserViewModelFactory @Inject constructor(private val repository: RoomRepository):
    ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(repository) as T
    }
}