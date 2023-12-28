package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.splash

import androidx.lifecycle.ViewModel
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.AuthorizeRepository

class SplashViewModel(private val authorizeRepository: AuthorizeRepository) : ViewModel() {
    fun getSession() = authorizeRepository.getSession()
}