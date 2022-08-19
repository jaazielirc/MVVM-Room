package com.jaax.edsa.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "password")
    var password: String,
    @ColumnInfo(name = "email")
    val email: String
)