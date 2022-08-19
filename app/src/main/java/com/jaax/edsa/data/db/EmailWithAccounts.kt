package com.jaax.edsa.data.db

import androidx.room.Embedded
import androidx.room.Relation
import com.jaax.edsa.data.model.Account
import com.jaax.edsa.data.model.Email

data class EmailWithAccounts(
    @Embedded
    val email: Email,
    @Relation(
        parentColumn = "address",
        entityColumn = "email"
    )
    val accounts: List<Account>
)
