package com.jaax.edsa.data.model

data class Email(
    var id: String,
    var name: String,
    var password: String,
    var accounts: ArrayList<Account>? = null
)