package com.jaax.edsa.data

import com.jaax.edsa.data.db.DatabaseEDSA
import com.jaax.edsa.data.model.Account
import com.jaax.edsa.data.model.Email
import com.jaax.edsa.data.model.User
import javax.inject.Inject

class RoomRepository @Inject constructor(private val room: DatabaseEDSA) {

    suspend fun saveuser(user: User) = room.userDao().insertUser(user)
    suspend fun getUser() = room.userDao().getUser()
    suspend fun updateUser(user: User) = room.userDao().updateUser(user)
    suspend fun deleteUser(user: User) = room.userDao().deleteUser(user)
    suspend fun dropEmailsByUser(username: String) = room.userDao().dropEmailsByUser(username)

    suspend fun saveEmail(email: Email) = room.emailDao().insertEmail(email)
    suspend fun getAllEmails(username: String) = room.emailDao().getEmailsByUser(username)
    suspend fun updateEmail(oldAddress: String, email: Email) =
        room.emailDao().updateEmail(oldAddress, email.address, email.password)
    suspend fun deleteEmail(email: Email) = room.emailDao().deleteEmail(email)
    suspend fun dropAccountsByEmail(email: String) = room.emailDao().dropAccountsByEmail(email)

    suspend fun saveAccount(account: Account) = room.accountDao().insertAccount(account)
    suspend fun getAllAccountsByEmail(email: String) = room.accountDao().getAccountsByEmail(email)
    suspend fun getLastAccountId(emailAddress: String) = room.accountDao().getLastAccountId(emailAddress)
    suspend fun updateAccount(account: Account) = room.accountDao().updateAccount(account)
    suspend fun deleteAccount(account: Account) = room.accountDao().deleteAccount(account)
    suspend fun updateAccountsByEmail(oldEmailAddresss: String, emailAddress: String) =
        room.accountDao().updateAccountsByEmail(oldEmailAddresss, emailAddress)
}