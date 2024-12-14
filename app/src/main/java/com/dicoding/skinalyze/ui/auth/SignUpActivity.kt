package com.dicoding.skinalyze.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.networking.RetrofitClient
import com.dicoding.skinalyze.ui.onboarding.SplashActivity
import com.dicoding.skinalyze.networking.SignUpRequest
import com.dicoding.skinalyze.networking.SignUpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val genderInput = findViewById<EditText>(R.id.genderInput)
        val dobInput = findViewById<EditText>(R.id.dobInput)

        btnSignUp.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val gender = genderInput.text.toString()
            val dob = dobInput.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
                gender.isNotEmpty() && dob.isNotEmpty()) {
                val request = SignUpRequest(name, email, password, gender, dob)
                signUp(request)
            } else {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUp(request: SignUpRequest) {
        RetrofitClient.instance.signUp(request).enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SignUpActivity, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignUpActivity, SplashActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@SignUpActivity, "Gagal: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
