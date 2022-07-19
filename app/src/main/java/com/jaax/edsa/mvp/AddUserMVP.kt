package com.jaax.edsa.mvp

import com.jaax.edsa.data.model.Email
import com.jaax.edsa.data.model.User

interface AddUserMVP {
    interface Model {
        fun createNewUser(name: String, password: String, keyword: String, emails: List<Email>?): User
        fun saveUser(user: User)
        fun getNewUser(): User
    }

    interface View {
        fun getUsername(): String
        fun getPassword(): String
        fun getKeyword(): String
        fun showSuccessAddUser()
        fun showErrorAddUserUsername()
        fun showErrorAddUserPassword()
        fun showErrorAddUserKeyword()
    }

    interface Presenter {
        fun setView(view: View)
        fun btnSaveClicked(): Boolean
        fun isValidUsername(): Boolean
        fun isValidPassword(): Boolean
        fun isValidKeyword(): Boolean
    }
}