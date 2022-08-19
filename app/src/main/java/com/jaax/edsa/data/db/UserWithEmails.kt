package com.jaax.edsa.data.db

import androidx.room.Embedded
import androidx.room.Relation
import com.jaax.edsa.data.model.Email
import com.jaax.edsa.data.model.User

data class UserWithEmails(
    @Embedded
    val user: User,
    @Relation(
        parentColumn = "name",
        entityColumn = "username"
    )
    val emails: List<Email>
)