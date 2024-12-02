package com.dicoding.skinalyze.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.ui.auth.SignInActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar!!.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(
                    this@SplashActivity, SignInActivity::class.java
                )
            )
            finish()
        }, 2000)
    }
}