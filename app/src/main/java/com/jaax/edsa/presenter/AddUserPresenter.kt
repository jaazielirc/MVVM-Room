package com.jaax.edsa.presenter
import com.jaax.edsa.data.model.AddUserModel
import com.jaax.edsa.mvp.AddUserMVP

class AddUserPresenter: AddUserMVP.Presenter {
    private var view: AddUserMVP.View? = null
    private var model: AddUserMVP.Model? = null

    init {
        model = AddUserModel()
    }

    override fun setView(view: AddUserMVP.View) {
        this.view = view
    }

    override fun btnSaveClicked(): Boolean {
        if(view != null) {
            return if( isValidUsername() && isValidPassword() && isValidKeyword() ) {
                val user = model!!.createNewUser(
                    view!!.getUsername(),
                    view!!.getPassword(),
                    view!!.getKeyword(),
                    null)
                model!!.saveUser(user)
                view!!.showSuccessAddUser()
                true
            } else {
                if(!isValidUsername()) view!!.showErrorAddUserUsername()
                if(!isValidPassword()) view!!.showErrorAddUserPassword()
                if(!isValidKeyword()) view!!.showErrorAddUserKeyword()
                false
            }
        }
        return false
    }

    override fun isValidUsername(): Boolean {
        return view!!.getUsername().trim() != ""
    }

    override fun isValidPassword(): Boolean {
        return view!!.getPassword().trim() != ""
    }

    override fun isValidKeyword(): Boolean {
        return view!!.getKeyword().trim() != ""
    }
}