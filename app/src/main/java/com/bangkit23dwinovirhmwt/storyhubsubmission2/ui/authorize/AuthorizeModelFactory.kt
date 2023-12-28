package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.authorize

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.AuthorizeRepository
import com.bangkit23dwinovirhmwt.storyhubsubmission2.di.Injection
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.splash.SplashViewModel

class AuthorizeModelFactory private constructor(private val authorizeRepository: AuthorizeRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authorizeRepository) as T
        }
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(authorizeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: AuthorizeModelFactory? = null

        fun getInstance(context: Context): AuthorizeModelFactory =
            instance ?: AuthorizeModelFactory(
                Injection.provideAuthRepository(context)
            )
    }
}