package com.jaax.edsa.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
data class Email(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "address")
    var address: String,
    @ColumnInfo(name = "password")
    var password: String,
    @ColumnInfo(name = "username")
    val username: String,
)