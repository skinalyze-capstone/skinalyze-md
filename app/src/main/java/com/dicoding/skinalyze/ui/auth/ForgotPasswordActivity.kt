package com.dicoding.skinalyze.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.skinalyze.R

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar?.hide()

        val btnResetPassword = findViewById<Button>(R.id.btnResetPassword)
        val tvBackToSignIn = findViewById<TextView>(R.id.tvBackToSignIn)

        btnResetPassword.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        tvBackToSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}
