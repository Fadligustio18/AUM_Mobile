package com.example.bknova.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.model.Tiket

class TiketAdapter(
    private var listTiket: List<Tiket>,
    private val onItemClick: (Tiket) -> Unit
) : RecyclerView.Adapter<TiketAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSiswa: TextView = view.findViewById(R.id.tv_siswa_tiket)
        val tvStatus: TextView = view.findViewById(R.id.tv_status_tiket)
        val tvJudul: TextView = view.findViewById(R.id.tv_judul_tiket)
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggal_tiket)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tiket, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tiket = listTiket[position]
        holder.tvSiswa.text = tiket.siswa ?: "Guru BK: ${tiket.bk}"
        holder.tvStatus.text = tiket.status
        holder.tvJudul.text = tiket.judul
        holder.tvTanggal.text = tiket.tanggalPembuatan

        // Apply Status Colors
        val context = holder.itemView.context
        val (bgRes, textColor) = when (tiket.status) {
            "Dikirim" -> R.drawable.bg_status_dikirim to R.color.brand_primary
            "Disetujui" -> R.drawable.bg_status_disetujui to R.color.green_text
            "Ditunda" -> R.drawable.bg_status_ditunda to R.color.accent_peach_text
            "Dibatalkan" -> R.drawable.bg_status_dibatalkan to R.color.red_text
            "Selesai" -> R.drawable.bg_status_selesai to R.color.brand_dark
            else -> R.drawable.bg_status_dikirim to R.color.text_color_secondary
        }

        holder.tvStatus.setBackgroundResource(bgRes)
        holder.tvStatus.setTextColor(ContextCompat.getColor(context, textColor))

        holder.itemView.setOnClickListener { onItemClick(tiket) }
    }

    override fun getItemCount(): Int = listTiket.size

    fun updateData(newList: List<Tiket>) {
        listTiket = newList
        notifyDataSetChanged()
    }
}
