package com.example.bknova.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bknova.R
import com.example.bknova.controller.AuthController
import com.example.bknova.model.Login
import com.example.bknova.model.LoginFeedback
import com.example.bknova.service.Aktor
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        
        authController = AuthController(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tilEmail = findViewById<TextInputLayout>(R.id.til_email)
        val tilPassword = findViewById<TextInputLayout>(R.id.til_password)
        val btnLogin = findViewById<MaterialButton>(R.id.btn_login)

        btnLogin.setOnClickListener {
            val email = tilEmail.editText?.text.toString().trim()
            val password = tilPassword.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = Login(email, password)
            
            authController.login(loginRequest, object : AuthController.LoginCallback {
                override fun onSuccess(user: LoginFeedback) {
                    val role = user.role.lowercase()
                    Toast.makeText(this@LoginActivity, "Selamat datang ${user.nama}", Toast.LENGTH_SHORT).show()

                    val intent = when (role) {
                        "siswa" -> Intent(this@LoginActivity, halaman_siswa_Activity::class.java)
                        "guru bk" -> Intent(this@LoginActivity, guruBkActivity::class.java)
                        else -> Intent(this@LoginActivity, LoginActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                }

                override fun onError(message: String) {
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}