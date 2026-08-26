package com.example.bknova.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.model.AumBidangHasil

class AumStatistikAdapter(
    private val listBidang: List<AumBidangHasil>
) : RecyclerView.Adapter<AumStatistikAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCount: TextView = view.findViewById(R.id.tv_stat_count)
        val tvLabel: TextView = view.findViewById(R.id.tv_stat_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aum_statistik, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bidang = listBidang[position]
        val count = bidang.pilihan.size
        holder.tvCount.text = count.toString()
        holder.tvLabel.text = bidang.namaBidang
        
        // Tetap berwarna biru sesuai tema, tidak berubah meski data > 5
        holder.tvCount.setTextColor(holder.itemView.context.getColor(R.color.brand_primary))
    }

    override fun getItemCount(): Int = listBidang.size
}