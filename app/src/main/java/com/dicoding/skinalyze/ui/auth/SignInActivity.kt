package com.dicoding.skinalyze.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.skinalyze.MainActivity
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.networking.LoginRequest
import com.dicoding.skinalyze.networking.LoginResponse
import com.dicoding.skinalyze.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide()

        // Referensi elemen UI
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        // Tombol Sign In
        btnSignIn.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password harus diisi!", Toast.LENGTH_SHORT).show()
            } else {
                val loginRequest = LoginRequest(email, password)
                performLogin(loginRequest)
            }
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

    // Fungsi untuk memanggil API login
    private fun performLogin(request: LoginRequest) {
        RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.user != null) {
                        Toast.makeText(this@SignInActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                        // Arahkan ke MainActivity
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@SignInActivity, loginResponse?.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SignInActivity, "Login gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Menambahkan fungsi untuk menangani logout
    fun logOut() {
        // Hapus data login yang tersimpan, seperti token atau sesi
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        // Menampilkan toast untuk konfirmasi logout
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()

        // Mengarahkan ke SignInActivity dan menghapus aktivitas sebelumnya
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Menutup SignInActivity atau aktivitas sebelumnya
        finish()
    }
}
