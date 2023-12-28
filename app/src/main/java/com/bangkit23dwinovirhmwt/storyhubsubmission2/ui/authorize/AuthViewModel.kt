package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.authorize

import androidx.lifecycle.ViewModel
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.AuthorizeRepository
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.preference.UserModel
import kotlinx.coroutines.flow.Flow


class AuthViewModel(private val authorizeRepository: AuthorizeRepository) : ViewModel() {
    var name: String? = null
    var email: String? = null
    var password: String? = null

    fun setNameValue(name: String) {
        this.name = name
    }

    fun setEmailValue(email: String) {
        this.email = email
    }

    fun setPasswordValue(password: String) {
        this.password = password
    }

    fun validateRegister(): Boolean {
        return name != null && email != null && password != null
    }

    fun registerUser(name: String, email: String, password: String) =
        authorizeRepository.registerUser(name, email, password)

    fun clearRegister(){
        this.name = null
        this.email = null
        this.password = null
    }

    fun validateLogin(): Boolean {
        return email != null && password != null
    }

    fun loginUser(email: String, password: String) = authorizeRepository.loginUser(email, password)

    fun clearLogin(){
        this.email = null
        this.password = null
    }

    fun getSession(): Flow<UserModel> = authorizeRepository.getSession()
}