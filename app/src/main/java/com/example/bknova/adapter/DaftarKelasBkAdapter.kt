package com.example.bknova.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.model.BkTask

class DaftarKelasBkAdapter(
    private val tasks: List<BkTask>,
    private val onClick: (BkTask) -> Unit
) : RecyclerView.Adapter<DaftarKelasBkAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaKelas: TextView = view.findViewById(R.id.tv_nama_kelas)
        val tvTingkatJurusan: TextView = view.findViewById(R.id.tv_tingkat_jurusan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kelas_bk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.tvNamaKelas.text = task.namaKelas
        holder.tvTingkatJurusan.text = "${task.tingkat} - ${task.tahunAjaran}"
        
        holder.itemView.setOnClickListener {
            onClick(task)
        }
    }

    override fun getItemCount(): Int = tasks.size
}
