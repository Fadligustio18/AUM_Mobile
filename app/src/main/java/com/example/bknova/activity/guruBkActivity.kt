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
import com.example.bknova.databinding.ActivityGuruBkBinding
import com.example.bknova.fragment.homeBkFragment
import com.example.bknova.fragment.profilBkFragment
import com.example.bknova.ui.WaveTransitionHelper

class guruBkActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityGuruBkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGuruBkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Hapus padding top pada root agar konten fragment bisa sampai ke atas (area baterai)
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            
            // Sesuaikan margin bawah navigasi card agar tidak tertutup tombol sistem
            val mlp = binding.navCardBk.layoutParams as android.view.ViewGroup.MarginLayoutParams
            // 24dp margin asli + tinggi navigasi sistem
            val marginInPx = (24 * resources.displayMetrics.density).toInt()
            mlp.bottomMargin = systemBars.bottom + marginInPx
            binding.navCardBk.layoutParams = mlp
            
            insets
        }

        // Set Default Fragment
        replaceFragment(homeBkFragment())
        updateNavUI(isHome = true)

        // Klik Home
        binding.btnNavHomeBk.setOnClickListener {
            replaceFragment(homeBkFragment())
            updateNavUI(isHome = true)
        }

        // Klik Profil
        binding.btnNavProfilBk.setOnClickListener {
            replaceFragment(profilBkFragment())
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
        binding.dotHomeBk.visibility = if (isHome) View.VISIBLE else View.INVISIBLE
        binding.icNavHomeBk.imageTintList = ColorStateList.valueOf(if (isHome) activeColor else inactiveColor)
        binding.tvNavHomeBk.setTextColor(if (isHome) activeColor else inactiveColor)
        binding.tvNavHomeBk.typeface = if (isHome) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT

        // UI Profil
        binding.dotProfilBk.visibility = if (!isHome) View.VISIBLE else View.INVISIBLE
        binding.icNavProfilBk.imageTintList = ColorStateList.valueOf(if (!isHome) activeColor else inactiveColor)
        binding.tvNavProfilBk.setTextColor(if (!isHome) activeColor else inactiveColor)
        binding.tvNavProfilBk.typeface = if (!isHome) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
    }

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        binding.navCardBk.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_bk, fragment)
            .commit()
    }
}
