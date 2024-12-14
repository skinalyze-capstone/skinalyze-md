package com.dicoding.skinalyze.ui.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.ui.auth.SignInActivity


class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar!!.hide()

        // Ambil preferensi dark mode dari SharedPreferences
        sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val isDarkModeEnabled = sharedPreferences.getBoolean("DARK_MODE", false)

        // Terapkan mode malam berdasarkan preferensi pengguna
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

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