package com.dicoding.skinalyze.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.skinalyze.MainActivity
import com.dicoding.skinalyze.R

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide()

        // Mendapatkan referensi ke elemen UI
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        // Tombol Sign In
        btnSignIn.setOnClickListener {
            // Tampilkan toast dan arahkan ke MainActivity
            Toast.makeText(this, "Sign in berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Teks Sign Up
        tvSignUp.setOnClickListener {
            // Arahkan ke halaman Sign Up
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Teks Forgot Password
        tvForgotPassword.setOnClickListener {
            // Arahkan ke halaman Forgot Password
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}

