package com.example.bknova.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.bknova.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val loadingCat = findViewById<LottieAnimationView>(R.id.loading_cat)
        val progressBar = findViewById<ProgressBar>(R.id.loading_progress)

        // Animasi Sinkron: Bar Loading & Kucing Berjalan
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 3000 // 3 detik
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            
            // 1. Update Progress Bar (0 - 100)
            progressBar.progress = (progress * 100).toInt()

            // 2. Update Posisi Kucing menggunakan Horizontal Bias
            // Ini akan membuat kucing bergerak dari sisi kiri (0.0) ke sisi kanan (1.0) bar
            val params = loadingCat.layoutParams as ConstraintLayout.LayoutParams
            params.horizontalBias = progress
            loadingCat.layoutParams = params
        }

        animator.start()

        // Pindah ke screen berikutnya setelah animasi selesai
        loadingCat.postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3200)
    }
}
