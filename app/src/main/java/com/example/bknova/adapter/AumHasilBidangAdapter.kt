package com.example.bknova.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.model.AumBidangHasil

class AumHasilBidangAdapter(
    private val listBidang: List<AumBidangHasil>
) : RecyclerView.Adapter<AumHasilBidangAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaBidang: TextView = view.findViewById(R.id.tv_nama_bidang)
        val tvPilihan: TextView = view.findViewById(R.id.tv_pilihan_masalah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aum_bidang_hasil, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bidang = listBidang[position]
        holder.tvNamaBidang.text = bidang.namaBidang
        
        val bulletedList = bidang.pilihan.joinToString("\n") { "• $it" }
        holder.tvPilihan.text = bulletedList

        setAnimation(holder.itemView)
    }

    private fun setAnimation(viewToAnimate: View) {
        val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.slide_in_left)
        viewToAnimate.startAnimation(animation)
    }

    override fun getItemCount(): Int = listBidang.size
}