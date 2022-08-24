package com.jaax.edsa.utils

class Utils {
    companion object {
        val validateUsername = Regex("(?=.*[a-zA-Z])\\S{4,10}")
        val validatePassword = Regex("(?=.*[a-zA-Z])\\S{4,20}")
    }
}