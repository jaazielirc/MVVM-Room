package com.jaax.edsa.data.db

import androidx.room.*
import com.jaax.edsa.data.model.Account

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAccount(account: Account)

    @Transaction
    @Query("SELECT * FROM emails WHERE address = :email LIMIT 1")
    suspend fun getAccountsByEmail(email: String): EmailWithAccounts

    @Query("SELECT id FROM accounts WHERE email = :emailAddress ORDER BY id DESC LIMIT 1")
    suspend fun getLastAccountId(emailAddress: String): Long?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAccount(account: Account)

    @Query("UPDATE accounts SET email = :emailAddress WHERE email = :oldEmailAddress")
    suspend fun updateAccountsByEmail(oldEmailAddress: String, emailAddress: String)

    @Delete
    suspend fun deleteAccount(account: Account)
}