package com.jaax.edsa.data.db

import androidx.room.*
import com.jaax.edsa.data.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query( "SELECT * FROM users LIMIT 1" )
    suspend fun getUser(): User?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM emails WHERE username = :username")
    suspend fun dropEmailsByUser(username: String)
}