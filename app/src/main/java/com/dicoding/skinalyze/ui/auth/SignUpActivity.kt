package com.dicoding.skinalyze.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.skinalyze.R

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val tvAlreadyHaveAccount = findViewById<TextView>(R.id.tvAlreadyHaveAccount)

        btnSignUp.setOnClickListener {
            Toast.makeText(this, "Anda berhasil mendaftar", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}
