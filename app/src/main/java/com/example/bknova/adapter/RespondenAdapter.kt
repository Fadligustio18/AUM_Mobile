package com.example.bknova.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.model.RespondenKuesioner

class RespondenAdapter(
    private var list: List<RespondenKuesioner>,
    private val onItemClick: (RespondenKuesioner) -> Unit
) : RecyclerView.Adapter<RespondenAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tv_siswa_nama)
        val tvNisn: TextView = view.findViewById(R.id.tv_siswa_nisn)
        val tvStatus: TextView = view.findViewById(R.id.tv_status_mengerjakan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_responden, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvNama.text = item.nama
        holder.tvNisn.text = "NISN: ${item.nisn}"
        
        val context = holder.itemView.context
        if (item.sudahMengerjakan) {
            holder.tvStatus.text = "Sudah Mengerjakan"
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_text))
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_disetujui)
        } else {
            holder.tvStatus.text = "Belum Mengerjakan"
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red_text))
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_dibatalkan)
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<RespondenKuesioner>) {
        list = newList
        notifyDataSetChanged()
    }
}
