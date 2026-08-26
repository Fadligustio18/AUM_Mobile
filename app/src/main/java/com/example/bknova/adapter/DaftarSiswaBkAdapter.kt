package com.example.bknova.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.model.Siswa

class DaftarSiswaBkAdapter(
    private val listSiswa: List<Siswa>,
    private val showArrow: Boolean = false,
    private val onClick: (Siswa) -> Unit
) : RecyclerView.Adapter<DaftarSiswaBkAdapter.ViewHolder>() {

    private var lastPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tv_nama_siswa)
        val tvNisn: TextView = view.findViewById(R.id.tv_nisn_siswa)
        val ivArrow: ImageView = view.findViewById(R.id.iv_arrow_next)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_siswa_bk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val siswa = listSiswa[position]
        holder.tvNama.text = siswa.nama
        holder.tvNisn.text = "NISN: ${siswa.nisn}"
        
        holder.ivArrow.visibility = if (showArrow) View.VISIBLE else View.GONE
        
        holder.itemView.setOnClickListener {
            onClick(siswa)
        }

        // Animasi muncul satu persatu saat di-scroll ke bawah
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // Jika posisi item baru (di-scroll ke bawah)
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun getItemCount(): Int = listSiswa.size
}
