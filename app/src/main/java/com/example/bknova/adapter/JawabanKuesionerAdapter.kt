package com.example.bknova.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.model.JawabanSiswaDetail

class JawabanKuesionerAdapter(
    private var list: List<JawabanSiswaDetail>
) : RecyclerView.Adapter<JawabanKuesionerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPertanyaan: TextView = view.findViewById(R.id.tv_pertanyaan)
        val tvJawaban: TextView = view.findViewById(R.id.tv_jawaban)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jawaban_kuesioner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvPertanyaan.text = item.pertanyaan
        holder.tvJawaban.text = if (item.tipe == "Pilihan Ganda") item.jawabanPG ?: "-" else item.jawabanEsai ?: "-"
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<JawabanSiswaDetail>) {
        list = newList
        notifyDataSetChanged()
    }
}
