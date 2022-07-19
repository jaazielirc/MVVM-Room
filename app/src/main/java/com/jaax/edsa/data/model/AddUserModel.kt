package com.jaax.edsa.data.model

import com.jaax.edsa.mvp.AddUserMVP

class AddUserModel: AddUserMVP.Model {

    override fun createNewUser(
        name: String,
        password: String,
        keyword: String,
        emails: List<Email>?
    ): User {
        return User(name, password, keyword, emails)
    }

    override fun saveUser(user: User) {
        //save user
    }

    override fun getNewUser(): User {
        return User("", "", "", null)
    }

}