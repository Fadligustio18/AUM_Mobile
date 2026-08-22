package com.example.bknova.activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.bknova.R
import com.example.bknova.databinding.ActivityHalamanSiswaBinding
import com.example.bknova.fragment.homeFragment
import com.example.bknova.fragment.profilFragment
import com.example.bknova.ui.WaveTransitionHelper

class halaman_siswa_Activity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHalamanSiswaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHalamanSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Hapus padding top pada root agar konten fragment bisa sampai ke atas (area baterai)
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            
            // Sesuaikan margin bawah navigasi card agar tidak tertutup tombol sistem
            val mlp = binding.navCard.layoutParams as android.view.ViewGroup.MarginLayoutParams
            // 24dp margin asli + tinggi navigasi sistem
            val marginInPx = (24 * resources.displayMetrics.density).toInt()
            mlp.bottomMargin = systemBars.bottom + marginInPx
            binding.navCard.layoutParams = mlp
            
            insets
        }

        // Set Default Fragment
        replaceFragment(homeFragment())
        updateNavUI(isHome = true)

        // Klik Home
        binding.btnNavHome.setOnClickListener {
            replaceFragment(homeFragment())
            updateNavUI(isHome = true)
        }

        // Klik Profil
        binding.btnNavProfil.setOnClickListener {
            replaceFragment(profilFragment())
            updateNavUI(isHome = false)
        }

        if (intent.getBooleanExtra("FROM_WAVE_TRANSITION", false)) {
            WaveTransitionHelper.finishTransition(this)
        }
    }

    private fun updateNavUI(isHome: Boolean) {
        val activeColor = ContextCompat.getColor(this, R.color.brand_primary)
        val inactiveColor = ContextCompat.getColor(this, R.color.text_color_secondary)

        // UI Home
        binding.dotHome.visibility = if (isHome) View.VISIBLE else View.INVISIBLE
        binding.icNavHome.imageTintList = ColorStateList.valueOf(if (isHome) activeColor else inactiveColor)
        binding.tvNavHome.setTextColor(if (isHome) activeColor else inactiveColor)
        binding.tvNavHome.typeface = if (isHome) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT

        // UI Profil
        binding.dotProfil.visibility = if (!isHome) View.VISIBLE else View.INVISIBLE
        binding.icNavProfil.imageTintList = ColorStateList.valueOf(if (!isHome) activeColor else inactiveColor)
        binding.tvNavProfil.setTextColor(if (!isHome) activeColor else inactiveColor)
        binding.tvNavProfil.typeface = if (!isHome) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
    }

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        binding.navCard.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
