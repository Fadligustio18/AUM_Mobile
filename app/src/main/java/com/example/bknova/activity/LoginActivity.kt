package com.example.bknova.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bknova.R
import com.example.bknova.model.Login
import com.example.bknova.model.LoginFeedback
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
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
            val sessionManager = SessionManager(this@LoginActivity)
            
            Aktor.auth.Login_Services(loginRequest).enqueue(object : Callback<LoginFeedback> {
                override fun onResponse(call: Call<LoginFeedback>, response: Response<LoginFeedback>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            // Simpan sesi login
                            sessionManager.saveSession(user.token, user.role, user.nama)

                            Toast.makeText(this@LoginActivity, "Selamat datang ${user.nama}", Toast.LENGTH_SHORT).show()

                            // Arahkan berdasarkan role
                            val intent = when (user.role.lowercase()) {
                                "admin" -> {
                                    // Ganti dengan AdminActivity jika sudah dibuat
                                    // Intent(this@LoginActivity, AdminActivity::class.java)
                                    Intent(this@LoginActivity, halaman_siswa_Activity::class.java) 
                                }
                                "siswa", "user" -> {
                                    Intent(this@LoginActivity, halaman_siswa_Activity::class.java)
                                }
                                else -> {
                                    Intent(this@LoginActivity, halaman_siswa_Activity::class.java)
                                }
                            }
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login gagal: Periksa kembali akun Anda", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginFeedback>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}