package com.jaax.edsa

class Utils {
    companion object {
        val regexValidUsername = Regex("(?=.*[a-zA-Z])\\S{4,15}")
        val regexValidPassword = Regex("(?=.*[a-zA-Z])\\S{6,100}")
        val regexValidKeyword = Regex("(?=.*[a-zA-Z])\\S{4,15}")
        val regexP = Regex("(?=.*[a-zA-Z])(?=.*\\d)(?=.*[,._*%-])\\S{8,100}")
        val regexK = Regex("(?=.*[a-zA-Z])(?=.*[,._*%-])\\S{6,15}")
    }
}