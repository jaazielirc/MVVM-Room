package com.jaax.edsa.data.model

data class User(
    var name: String,
    var password: String,
    var keyword: String,
    var emails: List<Email>? = null
)