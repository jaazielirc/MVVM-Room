package com.jaax.edsa.data.db

import androidx.room.*
import com.jaax.edsa.data.model.Email

@Dao
interface EmailDao {
    @Insert
    suspend fun insertEmail(email: Email)

    @Transaction
    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    suspend fun getEmailsByUser(name: String): UserWithEmails

    @Query("UPDATE emails SET address = :newAddress, password = :password WHERE address = :oldAddress")
    suspend fun updateEmail(oldAddress: String, newAddress: String, password: String)

    @Delete
    suspend fun deleteEmail(email: Email)

    @Query("DELETE FROM accounts WHERE email = :email")
    suspend fun dropAccountsByEmail(email: String)
}