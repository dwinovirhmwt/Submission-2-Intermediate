package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bangkit23dwinovirhmwt.storyhubsubmission2.R
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.authorize.AuthorizeModelFactory
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.authorize.login.LoginActivity
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.mainstory.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModels<SplashViewModel> {
        AuthorizeModelFactory.getInstance(this)
    }

    private val SPLASH_SCREEN_DURATION = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(SPLASH_SCREEN_DURATION)

            viewModel.getSession().collect { session ->
                if (session.isLogin) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}