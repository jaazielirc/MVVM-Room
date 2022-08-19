package com.jaax.edsa.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jaax.edsa.data.model.Account
import com.jaax.edsa.data.model.Email
import com.jaax.edsa.data.model.User

@Database(entities = [User::class, Email::class, Account::class], version = 1)
abstract class DatabaseEDSA: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun emailDao(): EmailDao
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var instance: DatabaseEDSA? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDB(context).also { instance = it }
        }

        private fun createDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                DatabaseEDSA::class.java,
                "edsa_db"
            ).build()
    }
}