package com.example.bknova.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.model.KuesionerSummary

class KuesionerAdapter(
    private var list: List<KuesionerSummary>,
    private val onItemClick: (KuesionerSummary) -> Unit
) : RecyclerView.Adapter<KuesionerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tv_judul_kuesioner)
        val tvDeskripsi: TextView = view.findViewById(R.id.tv_deskripsi_kuesioner)
        val tvKelas: TextView = view.findViewById(R.id.tv_info_kelas)
        val tvTahun: TextView = view.findViewById(R.id.tv_info_tahun)
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggal_kuesioner)
        val tvStatus: TextView = view.findViewById(R.id.tv_status_submit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kuesioner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvJudul.text = item.judul
        holder.tvDeskripsi.text = item.deskripsi
        holder.tvKelas.text = "Kelas: ${item.kelas}"
        holder.tvTahun.text = item.tahunAjaran
        holder.tvTanggal.text = item.createdAt

        // Status "Sudah Dikerjakan" for students
        if (item.sudahSubmit) {
            holder.tvStatus.visibility = View.VISIBLE
        } else {
            holder.tvStatus.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<KuesionerSummary>) {
        list = newList
        notifyDataSetChanged()
    }
}
