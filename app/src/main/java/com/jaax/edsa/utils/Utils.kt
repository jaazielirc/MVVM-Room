package com.jaax.edsa.utils

class Utils {
    companion object {
        val validateUsername = Regex("(?=.*[a-zA-Z])\\S{4,10}")
        val validatePassword = Regex("(?=.*[a-zA-Z])(?=.*\\d)\\S{6,20}")
        val validateKeyword = Regex("(?=.*[a-zA-Z])\\S{4,15}")
        val regexP = Regex("(?=.*[a-zA-Z])(?=.*\\d)(?=.*[,._*%-])\\S{8,20}")
        val regexK = Regex("(?=.*[a-zA-Z])(?=.*[,._*%-])\\S{4,15}")
    }
}