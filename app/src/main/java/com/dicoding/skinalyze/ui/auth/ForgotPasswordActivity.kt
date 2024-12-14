package com.dicoding.skinalyze.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.skinalyze.R

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar?.hide()
    }
}
