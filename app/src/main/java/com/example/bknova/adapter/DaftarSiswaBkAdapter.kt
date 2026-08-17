package com.example.bknova.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.model.Siswa

class DaftarSiswaBkAdapter(
    private val listSiswa: List<Siswa>,
    private val onClick: (Siswa) -> Unit
) : RecyclerView.Adapter<DaftarSiswaBkAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tv_nama_siswa)
        val tvNisn: TextView = view.findViewById(R.id.tv_nisn_siswa)
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
        
        holder.itemView.setOnClickListener {
            onClick(siswa)
        }
    }

    override fun getItemCount(): Int = listSiswa.size
}
