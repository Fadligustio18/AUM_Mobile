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
import com.example.bknova.model.UserResponse
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
                    authController.getUserProfile(object : AuthController.UserCallback {
                        override fun onSuccess(userResponse: UserResponse) {
                            authController.saveUserProfile(userResponse)
                            val role = userResponse.role
                            Toast.makeText(this@LoginActivity, "Selamat datang ${userResponse.nama}", Toast.LENGTH_SHORT).show()

                            val intent = when (role) {
                                "Siswa" -> Intent(this@LoginActivity, halaman_siswa_Activity::class.java)
                                "Guru BK", "Wali Kelas" -> Intent(this@LoginActivity, guruBkActivity::class.java)
                                else -> {
                                    Toast.makeText(this@LoginActivity, "Role tidak dikenali: $role", Toast.LENGTH_SHORT).show()
                                    null
                                }
                            }
                            
                            intent?.let {
                                startActivity(it)
                                finish()
                            }
                        }

                        override fun onError(message: String) {
                            Toast.makeText(this@LoginActivity, "Gagal memuat profil: $message", Toast.LENGTH_SHORT).show()
                        }
                    })
                }

                override fun onError(message: String) {
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}