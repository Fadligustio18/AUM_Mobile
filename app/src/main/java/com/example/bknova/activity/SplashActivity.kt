package com.example.bknova.activity

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.bknova.R
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Log FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM_TOKEN", "Token: $token")
            
            // If logged in, update token on the server
            val sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)
            if (sharedPref.getBoolean("is_logged_in", false)) {
                val authController = com.example.bknova.controller.AuthController(applicationContext)
                authController.updateFcmToken(token)
            }
        }

        // Animasi Sinkron: Bar Loading & Kucing Berjalan
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 3000 // 3 detik
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            


        }

        animator.start()

        // Pindah ke screen berikutnya setelah animasi selesai
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
            val role = sharedPref.getString("role", null)

            if (isLoggedIn) {
                val intent = when (role?.lowercase()) {
                    "siswa" -> Intent(this, halaman_siswa_Activity::class.java)
                    "guru bk", "wali kelas" -> Intent(this, guruBkActivity::class.java)
                    else -> Intent(this, LoginActivity::class.java)
                }
                
                // Pass notification data if available
                intent.putExtra("NOTIFICATION_TYPE", getIntent().getStringExtra("NOTIFICATION_TYPE"))
                intent.putExtra("NOTIFICATION_ID", getIntent().getStringExtra("NOTIFICATION_ID"))
                
                startActivity(intent)
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 3200)
    }
}
